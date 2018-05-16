/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.utils;

import com.jfinal.core.Const;
import com.jfinal.kit.PathKit;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * 类扫描器
 */
public class ClassScanner {

    private static final Set<Class> appClasses = new HashSet<>();

    public static <T> List<Class<T>> scanSubClass(Class<T> pclazz) {
        return scanSubClass(pclazz, false);
    }

    private static final Set<String> excludeJars = new HashSet<>();

    static {
        excludeJars.add("animal-sniffer-annotations-");
        excludeJars.add("aopalliance-");
        excludeJars.add("archaius-core-");
        excludeJars.add("assertj-core-");
        excludeJars.add("brave-");
        excludeJars.add("cglib-nodep-");
        excludeJars.add("consul-api-");
        excludeJars.add("cos-2017.5.jar");
        excludeJars.add("druid-");
        excludeJars.add("ehcache-");
        excludeJars.add("error_prone_annotations-");
        excludeJars.add("fastjson-");
        excludeJars.add("commons-beanutils");
        excludeJars.add("commons-codec");
        excludeJars.add("commons-collections");
        excludeJars.add("commons-configuration");
        excludeJars.add("commons-lang");
        excludeJars.add("commons-logging");
        excludeJars.add("commons-pool");
        excludeJars.add("commons-io");
        excludeJars.add("commons-httpclient");
        excludeJars.add("commons-fileupload");
        excludeJars.add("commons-configuration");
        excludeJars.add("commons-validator");
        excludeJars.add("commons-email");
        excludeJars.add("fst-");
        excludeJars.add("gson-");
        excludeJars.add("guava-");
        excludeJars.add("guice-");
        excludeJars.add("HdrHistogram-");
        excludeJars.add("HikariCP-");
        excludeJars.add("httpclient-");
        excludeJars.add("httpcore-");
        excludeJars.add("hystrix-");
        excludeJars.add("j2objc-annotations-");
        excludeJars.add("jackson-module-afterburner-");
        excludeJars.add("javapoet-");
        excludeJars.add("javassist-");
        excludeJars.add("javax.");
        excludeJars.add("org.apache.");
        excludeJars.add("com.sun.");
        excludeJars.add("jboss-");
        excludeJars.add("jedis-");
        excludeJars.add("jfinal-");
        excludeJars.add("joda-time-");
        excludeJars.add("jsoup-");
        excludeJars.add("jsr305-");
        excludeJars.add("metrics-");
        excludeJars.add("motan-");
        excludeJars.add("mysql-connector-java-");
        excludeJars.add("netty-");
        excludeJars.add("objenesis-");
        excludeJars.add("opentracing-");
        excludeJars.add("profiler-");
        excludeJars.add("rxjava-");
        excludeJars.add("sharding-jdbc-core-");
        excludeJars.add("servlet-");
        excludeJars.add("shiro-");
        excludeJars.add("slf4j-");
        excludeJars.add("spring-");
        excludeJars.add("zipkin-");
        excludeJars.add("jcommander-");
        excludeJars.add("jackson-");
        excludeJars.add("org.eclipse.");
        excludeJars.add("jetty-");
        excludeJars.add("freemarker-");
        excludeJars.add("dom4j-");
        excludeJars.add("amqp-client-");
        excludeJars.add("ons-client-");
        excludeJars.add("hamcrest-core-");
        excludeJars.add("mchange-commons-java-");
        excludeJars.add("idea_rt.jar");
        excludeJars.add("MRJToolkit.jar");
        excludeJars.add("struts-");
        excludeJars.add("c3p0-");
        excludeJars.add("junit-");
        excludeJars.add("javase-");
        excludeJars.add("antlr-");
        excludeJars.add("velocity-");
        excludeJars.add("log4j-");
        excludeJars.add("dubbo-");
        excludeJars.add("cron4j-");
        excludeJars.add("sslext-");
        excludeJars.add("logback-");
        excludeJars.add("metrics-");
        excludeJars.add("jline-");
        excludeJars.add("zkclient-");
        excludeJars.add("okhttp-");
        excludeJars.add("okio-");
        excludeJars.add("zbus-");
        excludeJars.add("hessian-");
        excludeJars.add("groovy-");
        excludeJars.add("snakeyaml-");
        excludeJars.add("kryo-");
        excludeJars.add("reflectasm-");
        excludeJars.add("asm-");
        excludeJars.add("minlog-");
        excludeJars.add("swagger-");
        excludeJars.add("validation-api-");
        excludeJars.add("checker-compat-qual-");
        excludeJars.add("caffeine-");
        excludeJars.add("j2cache-core-");
        excludeJars.add("jgroups-");
        excludeJars.add("snappy-java-");
        excludeJars.add("resteasy-");
        excludeJars.add("activation-");
        excludeJars.add("jcip-annotations-");
        excludeJars.add("jjwt-");
        excludeJars.add("undertow-");
        excludeJars.add("reactor-core-");
        excludeJars.add("reactive-streams-");
        excludeJars.add("lettuce-core-");
        excludeJars.add("xnio-");
        excludeJars.add("wrapper.jar");
    }

    private static final Set<String> excludeJarPackages = new HashSet<>();

    static {
        excludeJarPackages.add("com.google");
        excludeJarPackages.add("oro.oro");
        excludeJarPackages.add("org.eclipse");
        excludeJarPackages.add("org.apache");
        excludeJarPackages.add("org.osgi");
    }

    public static <T> List<Class<T>> scanSubClass(Class<T> pclazz, boolean mustCanNewInstance) {
        if (pclazz == null) {
            throw new RuntimeException("pclazz cannot be null");
        }

        if (appClasses.isEmpty()) {
            initAppClasses();
        }

        List<Class<T>> classes = new ArrayList<>();
        findClassesByParent(classes, pclazz, mustCanNewInstance);
        return classes;
    }

    public static List<Class> scanClass() {
        return scanClass(false);
    }

    public static List<Class> scanClass(boolean mustCanNewInstance) {

        if (appClasses.isEmpty()) {
            initAppClasses();
        }

        List<Class> list = new ArrayList<>();

        if (mustCanNewInstance) {
            for (Class clazz : appClasses) {
                if (clazz.isInterface()
                        || Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }
                list.add(clazz);
            }
        } else {
            list.addAll(appClasses);
        }

        return list;
    }

    public static List<Class> scanClassByAnnotation(Class annotationClass, boolean mustCanNewInstance) {

        if (appClasses.isEmpty()) {
            initAppClasses();
        }

        List<Class> list = new ArrayList<>();

        for (Class clazz : appClasses) {
            Annotation annotation = clazz.getAnnotation(annotationClass);
            if (annotation == null) {
                continue;
            }

            if (mustCanNewInstance) {
                if (clazz.isInterface()
                        || Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }
            }

            list.add(clazz);
        }
        return list;
    }


    /**
     * 开发环境下，用于热加载后重新清空所有的类
     */
    static void clearAppClasses() {
        appClasses.clear();
    }

    private static <T> void findClassesByParent(List<Class<T>> classes, Class<T> pclazz, boolean mustCanNewInstance) {
        for (Class clazz : appClasses) {
            tryToaddClass(classes, pclazz, mustCanNewInstance, clazz);
        }
    }

    private static void initAppClasses() {

        initByFilePath(PathKit.getRootClassPath());

        Set<String> jars = new HashSet<>();
        findJars(jars, ClassScanner.class.getClassLoader());

        for (String path : jars) {

            JarFile jarFile = null;
            try {
                jarFile = new JarFile(path);
                if (isExcluedeJar(jarFile.getManifest())) {
                    continue;
                }
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String entryName = jarEntry.getName();
                    if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
                        String className = entryName.replace("/", ".").substring(0, entryName.length() - 6);
                        initAppClasses(classForName(className));
                    }
                }
            } catch (IOException e1) {
            } finally {
                if (jarFile != null)
                    try {
                        jarFile.close();
                    } catch (IOException e) {
                    }
            }

        }
    }

    private static boolean isExcluedeJar(Manifest manifest) {
        Attributes mainAttributes = manifest.getMainAttributes();
        if (mainAttributes == null) {
            return false;
        }

        String exportPackage = mainAttributes.getValue("Export-Package");
        if (exportPackage != null) {
            if (exportPackage.startsWith("com.google.")
                    || exportPackage.startsWith("org.apache.")
                    || exportPackage.startsWith("org.jboss.")
                    || exportPackage.startsWith("com.netflix.")
                    || exportPackage.startsWith("com.github.")
                    || exportPackage.startsWith("org.eclipse.")
                    || exportPackage.startsWith("com.fasterxml.")
                    || exportPackage.startsWith("org.slf4j")
                    || exportPackage.startsWith("net.sf")
                    ) {
                return true;
            }
        }

        String vendor = mainAttributes.getValue("Implementation-Vendor");
        if (vendor != null) {
            vendor = vendor.toLowerCase();
            if (vendor.indexOf("jboss") > -1
                    || vendor.indexOf("apache") > -1
                    || vendor.indexOf("oracle") > -1
                    || vendor.indexOf("netty") > -1
                    || vendor.indexOf("dubbo") > -1
                    ) {
                return true;
            }
        }

        return false;
    }

    private static boolean isExcludeJar(String path) {

        if (!path.toLowerCase().endsWith(".jar")) {
            return true;
        }

        for (String exclude : excludeJarPackages) {
            if (path.contains(exclude.replace(".", File.separator))) {
                return true;
            }
        }
        for (String exclude : excludeJars) {
            if (new File(path).getName().startsWith(exclude)) {
                return true;
            }
        }

        return false;
    }

    private static void initByFilePath(String filePath) {


        List<File> classFileList = new ArrayList<>();
        scanClassFile(classFileList, filePath);
        for (File file : classFileList) {

            int start = filePath.length();
            int end = file.toString().length() - ".class".length();

            String classFile = file.toString().substring(start + 1, end);
            String className = classFile.replace(File.separator, ".");

            initAppClasses(classForName(className));
        }
    }

    private static void initAppClasses(Class clazz) {
        if (clazz != null)
            appClasses.add(clazz);
    }


    private static void findJars(Set<String> set, ClassLoader classLoader) {
        try {
            if (classLoader instanceof URLClassLoader) {
                URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
                URL[] urLs = urlClassLoader.getURLs();
                String JAVA_HOME = new File(System.getProperty("java.home"), "..").getCanonicalPath();
                for (URL url : urLs) {
                    String path = url.getPath();
                    path = URLDecoder.decode(path, Const.DEFAULT_ENCODING);

                    // path : /d:/xxx
                    if (path.startsWith("/") && path.indexOf(":") == 2) {
                        path = path.substring(1);
                    }

                    if (!path.toLowerCase().endsWith(".jar")) {
                        initByFilePath(new File(path).getCanonicalPath());
                    }

                    if (!path.startsWith(JAVA_HOME) && !isExcludeJar(path)) {
                        set.add(url.getPath());
                    }
                }
            }
            ClassLoader parent = classLoader.getParent();
            if (parent != null) {
                findJars(set, parent);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private static <T> void tryToaddClass(List<Class<T>> classes, Class<T> pclazz, boolean mustCanNewInstance, Class<T> clazz) {
        if (classes == null
                || pclazz == null
                || clazz == null
                || !pclazz.isAssignableFrom(clazz)) {

            return;
        }

        if (!mustCanNewInstance) {
            classes.add(clazz);
            return;
        }

        if (clazz.isInterface()
                || Modifier.isAbstract(clazz.getModifiers())) {
            return;
        }

        classes.add(clazz);
    }


    @SuppressWarnings("unchecked")
    private static Class classForName(String className) {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            return Class.forName(className, false, cl);
        } catch (Throwable ex) {
            //ignore
        }
        return null;
    }

    private static void scanClassFile(List<File> fileList, String path) {
        File files[] = new File(path).listFiles();
        if (null == files || files.length == 0)
            return;
        for (File file : files) {
            if (file.isDirectory()) {
                scanClassFile(fileList, file.getAbsolutePath());
            } else if (file.getName().endsWith(".class")) {
                fileList.add(file);
            }
        }
    }


}
