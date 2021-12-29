/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.test;

import com.jfinal.config.JFinalConfig;
import com.jfinal.core.JFinalFilter;
import com.jfinal.kit.PathKit;
import io.jboot.aop.JbootAopFactory;
import io.jboot.aop.cglib.JbootCglibProxyFactory;
import io.jboot.app.PathKitExt;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.test.web.MockFilterChain;
import io.jboot.test.web.MockFilterConfig;
import io.jboot.test.web.MockJFinalFilter;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.FileUtil;
import io.jboot.utils.ReflectUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class MockApp {

    public static final String DEFAULT_WEB_ROOT_PATH = "../classes/webapp";
    public static final String DEFAULT_CLASS_PATH = "../classes";

    private static final MockApp app = new MockApp();

    private JFinalConfig config;
    private final JFinalFilter filter;

    private Object testInstance;


    private MockApp() {
        filter = new MockJFinalFilter();
    }

    public static MockApp getInstance() {
        return app;
    }

    static void mockRequest(ServletRequest req, ServletResponse res) {
        try {
            app.filter.doFilter(req, res, new MockFilterChain());
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }

    public void setTestInstance(Object testInstance) {
        this.testInstance = testInstance;
    }

    public Object getTestInstance() {
        return testInstance;
    }

    void start(Class<?> testClass) {
        try {
            TestConfig testConfig = testClass.getAnnotation(TestConfig.class);

            if (testConfig != null) {
                JbootConfigManager.parseArgs(testConfig.launchArgs());
                JbootConfigManager.me().setDevMode(testConfig.devMode());
                ClassScanner.setPrintScannerInfoEnable(testConfig.printScannerInfo());
            } else {
                ClassScanner.setPrintScannerInfoEnable(false);
            }

            doInitJFinalPathKit(testConfig);


            boolean autoMockInterface = testConfig != null && testConfig.autoMockInterface();
            JbootCglibProxyFactory.setMethodInterceptor(aClass -> new MockMethodInterceptor(autoMockInterface));

            List<MockMethodInfo> mockMethodInfos = getMockMethodInfoList(testClass);
            if (!mockMethodInfos.isEmpty()) {
                mockMethodInfos.forEach(MockMethodInterceptor::addMethodInfo);
            }

            List<MockClassInfo> mockClassInfos = getMockClassInfoList(testClass);
            if (mockClassInfos != null && !mockClassInfos.isEmpty()) {
                mockClassInfos.forEach(mockClassInfo -> {
                    JbootAopFactory.me().addMapping(mockClassInfo.getTargetClass(), mockClassInfo.getMockClass());
                    MockMethodInterceptor.addMockClass(mockClassInfo.getMockClass());
                });
            }

            filter.init(new MockFilterConfig());
            config = ReflectUtil.getFieldValue(filter, "jfinalConfig");
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    private List<MockClassInfo> getMockClassInfoList(Class<?> testClass) {
        List<Class> scanedClasses = ClassScanner.scanClassByAnnotation(MockClass.class, true);

        LinkedList<Class> mockClasses = new LinkedList<>(scanedClasses);
        Class<?>[] declardClasses = testClass.getDeclaredClasses();
        if (declardClasses.length > 0) {
            for (Class<?> declardClass : declardClasses) {
                if (declardClass.getAnnotation(MockClass.class) != null) {
                    mockClasses.remove(declardClass);
                    mockClasses.addLast(declardClass);
                }
            }
        }

        if (mockClasses.isEmpty()) {
            return null;
        }

        List<MockClassInfo> classInfoList = new ArrayList<>();
        for (Class<?> mockClass : mockClasses) {
//            MockClass annotation = mockClass.getAnnotation(MockClass.class);
//            Class<?>[] targetClasses = annotation.value();
//            if (targetClasses.length == 0) {
//                targetClasses = mockClass.getInterfaces();
//            }
            Class<?>[] targetClasses = mockClass.getInterfaces();
            if (targetClasses.length == 0) {
                throw new IllegalStateException("@MockClass() in \"" + mockClass.getName() + "\" must implementation interface.");
            }

            for (Class<?> targetClass : targetClasses) {
                classInfoList.add(new MockClassInfo(mockClass, targetClass));
            }
        }

        return classInfoList;
    }

    private List<MockMethodInfo> getMockMethodInfoList(Class<?> testClass) {
        List<MockMethodInfo> methodInfoList = new ArrayList<>();
        searchMockMethods(testClass, methodInfoList);
        return methodInfoList;
    }


    private void searchMockMethods(Class<?> searchClass, List<MockMethodInfo> tolist) {
        Method[] methods = searchClass.getDeclaredMethods();
        for (Method method : methods) {
            MockMethod mockMethod = method.getAnnotation(MockMethod.class);
            if (mockMethod != null) {
                tolist.add(new MockMethodInfo(searchClass, method, mockMethod));
            }
        }

        Class<?> superClass = searchClass.getSuperclass();
        if (superClass != Object.class && superClass != null) {
            searchMockMethods(superClass, tolist);
        }
    }


    void stop() {
        if (config != null) {
            config.onStop();
        }
    }


    private void doInitJFinalPathKit(TestConfig testConfig) {
        try {
            String configWebRootPath = testConfig != null ? testConfig.webRootPath() : DEFAULT_WEB_ROOT_PATH;
            String configClassPath = testConfig != null ? testConfig.classPath() : DEFAULT_CLASS_PATH;

            //相对路径，是相对 /target/test-classes 进行判断的
            if (!FileUtil.isAbsolutePath(configWebRootPath)) {
                configWebRootPath = new File(PathKitExt.getWebRootPath(), configWebRootPath).getCanonicalPath();
            }
            //设置 webRootPath
            PathKit.setWebRootPath(configWebRootPath);


            if (!FileUtil.isAbsolutePath(configClassPath)) {
                configClassPath = new File(PathKitExt.getRootClassPath(), configClassPath).getCanonicalPath();
            }
            //设置 classPath
            PathKit.setRootClassPath(configClassPath);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}
