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
import io.jboot.app.PathKitExt;
import io.jboot.test.web.MockFilterChain;
import io.jboot.test.web.MockFilterConfig;
import io.jboot.utils.ReflectUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

public class MockApp {

    private static MockApp app = new MockApp();

    private JFinalConfig config;
    private JFinalFilter filter;


    private MockApp() {
        filter = new JFinalFilter();
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

    public void start() {
        try {
            doInitJFinalPathKit();
            filter.init(new MockFilterConfig());
            config = ReflectUtil.getFieldValue(JFinalFilter.class, "jfinalConfig", filter);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }


    public void stop() {
        config.onStop();
    }


    private void doInitJFinalPathKit() {
        try {
            Class<?> c = MockApp.class.getClassLoader().loadClass("com.jfinal.kit.PathKit");
            Method setWebRootPath = c.getMethod("setWebRootPath", String.class);
            String webRootPath = PathKitExt.getWebRootPath();
            setWebRootPath.invoke(null, webRootPath);

            // -------
            Method setRootClassPath = c.getMethod("setRootClassPath", String.class);
            String rootClassPath = PathKitExt.getRootClassPath();
            setRootClassPath.invoke(null, rootClassPath);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}
