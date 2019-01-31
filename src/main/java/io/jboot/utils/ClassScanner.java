/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import io.jboot.app.config.JbootConfigManager;
import io.jboot.app.config.annotation.ConfigModel;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类扫描器
 */
public class ClassScanner {

    private static Config config = JbootConfigManager.me().get(Config.class);
    private static final Set<Class> appClasses = new HashSet<>();
    public static final Set<String> includeJars = new HashSet<>();
    public static final Set<String> excludeJars = new HashSet<>();

    public static void addScanJarPrefix(String prefix) {
        includeJars.add(prefix.trim());
    }

    public static void addUnscanJarPrefix(String prefix) {
        excludeJars.add(prefix.trim());
    }

    static {
        excludeJars.add("jfinal-");
        excludeJars.add("cos-2017.5.jar");
        excludeJars.add("cglib-");
        excludeJars.add("undertow-");
        excludeJars.add("xnio-");
        excludeJars.add("javax.");
        excludeJars.add("HikariCP-");
        excludeJars.add("druid-");
        excludeJars.add("mysql-");
        excludeJars.add("db2jcc-");
        excludeJars.add("db2jcc4-");
        excludeJars.add("ojdbc");
        excludeJars.add("junit-");
        excludeJars.add("hamcrest-");
        excludeJars.add("jboss-");
        excludeJars.add("motan-");
        excludeJars.add("commons-pool");
        excludeJars.add("commons-pool2");
        excludeJars.add("commons-beanutils");
        excludeJars.add("commons-codec");
        excludeJars.add("commons-collections");
        excludeJars.add("commons-configuration");
        excludeJars.add("commons-lang");
        excludeJars.add("commons-logging");
        excludeJars.add("commons-io");
        excludeJars.add("commons-httpclient");
        excludeJars.add("commons-fileupload");
        excludeJars.add("commons-validator");
        excludeJars.add("commons-email");
        excludeJars.add("commons-text");
        excludeJars.add("hessian-");
        excludeJars.add("metrics-");
        excludeJars.add("javapoet-");
        excludeJars.add("netty-");
        excludeJars.add("consul-");
        excludeJars.add("gson-");
        excludeJars.add("zookeeper-");
        excludeJars.add("slf4j-");
        excludeJars.add("fastjson-");
        excludeJars.add("guava-");
        excludeJars.add("failureaccess-");
        excludeJars.add("listenablefuture-");
        excludeJars.add("jsr305-");
        excludeJars.add("checker-qual-");
        excludeJars.add("error_prone_annotations-");
        excludeJars.add("j2objc-");
        excludeJars.add("animal-sniffer-");
        excludeJars.add("cron4j-");
        excludeJars.add("jedis-");
        excludeJars.add("lettuce-");
        excludeJars.add("reactor-");
        excludeJars.add("fst-");
        excludeJars.add("kryo-");
        excludeJars.add("jackson-");
        excludeJars.add("javassist-");
        excludeJars.add("objenesis-");
        excludeJars.add("reflectasm-");
        excludeJars.add("asm-");
        excludeJars.add("minlog-");
        excludeJars.add("jsoup-");
        excludeJars.add("ons-client-");
        excludeJars.add("amqp-client-");
        excludeJars.add("ehcache-");
        excludeJars.add("sharding-");
        excludeJars.add("snakeyaml-");
        excludeJars.add("groovy-");
        excludeJars.add("profiler-");
        excludeJars.add("joda-time-");
        excludeJars.add("shiro-");
        excludeJars.add("dubbo-");
        excludeJars.add("curator-");
        excludeJars.add("resteasy-");
        excludeJars.add("reactive-");
        excludeJars.add("validation-");
        excludeJars.add("httpclient-");
        excludeJars.add("httpcore-");
        excludeJars.add("jcip-");
        excludeJars.add("jcl-");
        excludeJars.add("microprofile-");
        excludeJars.add("org.osgi");
        excludeJars.add("zkclient-");
        excludeJars.add("jjwt-");
        excludeJars.add("okhttp-");
        excludeJars.add("okio-");
        excludeJars.add("zbus-");
        excludeJars.add("swagger-");
        excludeJars.add("j2cache-");
        excludeJars.add("caffeine-");
        excludeJars.add("jline-");
        excludeJars.add("qpid-");
        excludeJars.add("geronimo-");
        excludeJars.add("activation-");
        excludeJars.add("org.abego");
        excludeJars.add("antlr-");
        excludeJars.add("antlr4-");
        excludeJars.add("ST4-");
        excludeJars.add("icu4j-");
        excludeJars.add("idea_rt");
        excludeJars.add("MRJToolkit");
        excludeJars.add("logback-");
        excludeJars.add("log4j-");
        excludeJars.add("log4j2-");
        excludeJars.add("aliyun-java-sdk-");
        excludeJars.add("aliyun-sdk-");
        excludeJars.add("archaius-");
        excludeJars.add("aopalliance-");
        excludeJars.add("HdrHistogram-");
        excludeJars.add("jdom-");
        excludeJars.add("rxjava-");
        excludeJars.add("jersey-");
        excludeJars.add("stax-");
        excludeJars.add("stax2-");
        excludeJars.add("jettison-");
        excludeJars.add("commonmark-");
        excludeJars.add("jaxb-");
        excludeJars.add("json-20");
        excludeJars.add("jcseg-");
        excludeJars.add("lucene-");
        excludeJars.add("elasticsearch-");
        excludeJars.add("jopt-");
        excludeJars.add("httpasyncclient-");
        excludeJars.add("jna-");
        excludeJars.add("lang-mustache-client-");
        excludeJars.add("parent-join-client-");
        excludeJars.add("rank-eval-client-");
        excludeJars.add("aggs-matrix-stats-client-");
        excludeJars.add("t-digest-");
        excludeJars.add("compiler-");
        excludeJars.add("hppc-");
        excludeJars.add("libthrift-");

    }

    static {
        for (String prefix : config.getScanJarPrefixes()) {
            addScanJarPrefix(prefix);
        }
        for (String prefix : config.getUnScanJarPrefixes()) {
            addUnscanJarPrefix(prefix);
        }
    }

    public static <T> List<Class<T>> scanSubClass(Class<T> pclazz) {
        return scanSubClass(pclazz, false);
    }


    public static <T> List<Class<T>> scanSubClass(Class<T> pclazz, boolean mustCanNewInstance) {
        initIfNecessary();
        List<Class<T>> classes = new ArrayList<>();
        findClassesByParent(classes, pclazz, mustCanNewInstance);
        return classes;
    }

    public static List<Class> scanClass() {
        return scanClass(false);
    }

    public static List<Class> scanClass(boolean mustCanNewInstance) {

        initIfNecessary();

        if (!mustCanNewInstance) {
            return new ArrayList<>(appClasses);
        }

        List<Class> list = new ArrayList<>();
        for (Class clazz : appClasses) {
            if (canNewInstance(clazz)) {
                list.add(clazz);
            }
        }

        return list;
    }


    private static boolean canNewInstance(Class clazz) {
        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
    }


    public static List<Class> scanClassByAnnotation(Class annotationClass, boolean mustCanNewInstance) {
        initIfNecessary();

        List<Class> list = new ArrayList<>();
        for (Class clazz : appClasses) {
            Annotation annotation = clazz.getAnnotation(annotationClass);
            if (annotation == null) {
                continue;
            }

            if (mustCanNewInstance && !canNewInstance(clazz)) {
                continue;
            }

            list.add(clazz);
        }

        return list;
    }

    private static void initIfNecessary() {
        if (appClasses.isEmpty()) {
            initAppClasses();
        }
    }


    private static <T> void findClassesByParent(List<Class<T>> classes, Class<T> pclazz, boolean mustCanNewInstance) {
        for (Class clazz : appClasses) {

            if (!pclazz.isAssignableFrom(clazz)) {
                continue;
            }

            if (mustCanNewInstance && !canNewInstance(clazz)) {
                continue;
            }

            classes.add(clazz);
        }
    }


    private static void initAppClasses() {

        Set<String> jarPaths = new HashSet<>();
        Set<String> classPaths = new HashSet<>();

        findClassPathsAndJars(jarPaths, classPaths, ClassScanner.class.getClassLoader());

        for (String jarPath : jarPaths) {
            if (JbootConfigManager.me().isDevMode()) {
                System.out.println("ClassScanner scan jar : " + jarPath);
            }
            addClassesFromJar(jarPath);
        }

        for (String classPath : classPaths) {
            if (JbootConfigManager.me().isDevMode()) {
                System.out.println("ClassScanner scan classpath : " + classPath);
            }
            addClassesFromClassPath(classPath);
        }
    }

    private static void addClassesFromJar(String jarPath) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPath);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName();
                if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
                    String className = entryName.replace("/", ".").substring(0, entryName.length() - 6);
                    addClass(classForName(className));
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


    private static void addClassesFromClassPath(String classPath) {

        List<File> classFileList = new ArrayList<>();
        scanClassFile(classFileList, classPath);

        for (File file : classFileList) {

            int start = classPath.length();
            int end = file.toString().length() - ".class".length();

            String classFile = file.toString().substring(start + 1, end);
            String className = classFile.replace(File.separator, ".");

            addClass(classForName(className));
        }
    }

    private static void addClass(Class clazz) {
        if (clazz != null) appClasses.add(clazz);
    }


    private static void findClassPathsAndJars(Set<String> jarPaths, Set<String> classPaths, ClassLoader classLoader) {
        try {
            if (classLoader instanceof URLClassLoader) {
                URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
                URL[] urLs = urlClassLoader.getURLs();
                for (URL url : urLs) {
                    String path = url.getPath();
                    path = URLDecoder.decode(path, "UTF-8");

                    // path : /d:/xxx
                    if (path.startsWith("/") && path.indexOf(":") == 2) {
                        path = path.substring(1);
                    }

                    if (!path.toLowerCase().endsWith(".jar")) {
                        classPaths.add(new File(path).getCanonicalPath());
                        continue;
                    }

                    if (isIncludeJar(path)) {
                        jarPaths.add(path);
                    }
                }
            }
            ClassLoader parent = classLoader.getParent();
            if (parent != null) {
                findClassPathsAndJars(jarPaths, classPaths, parent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static boolean isIncludeJar(String path) {

        String jarName = new File(path).getName();

        for (String include : includeJars) {
            if (jarName.startsWith(include)) {
                return true;
            }
        }

        for (String exclude : excludeJars) {
            if (jarName.startsWith(exclude)) {
                return false;
            }
        }

        if (path.startsWith(getJavaHome())) {
            return false;
        }

        if (isJrelibPath(path)) {
            return false;
        }

        return true;
    }


    private static boolean isJrelibPath(String path) {
        path = path.toLowerCase();
        return path.indexOf("/jre/lib/") > -1 || path.indexOf("\\jre\\lib\\") > -1;
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


    private static String javaHome;

    private static String getJavaHome() {
        if (javaHome == null) {
            try {
                javaHome = new File(System.getProperty("java.home"), "..").getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return javaHome;
    }

    @ConfigModel(prefix = "jboot.app.scanner")
    public static class Config {

        private String scanJarPrefix;
        private String unScanJarPrefix;

        public String getScanJarPrefix() {
            return scanJarPrefix;
        }

        public void setScanJarPrefix(String scanJarPrefix) {
            this.scanJarPrefix = scanJarPrefix;
        }

        public String getUnScanJarPrefix() {
            return unScanJarPrefix;
        }

        public void setUnScanJarPrefix(String unScanJarPrefix) {
            this.unScanJarPrefix = unScanJarPrefix;
        }

        public List<String> getScanJarPrefixes() {
            return readFrom(scanJarPrefix);
        }

        public List<String> getUnScanJarPrefixes() {
            return readFrom(unScanJarPrefix);
        }

        private List<String> readFrom(String data) {
            List<String> prefixes = new ArrayList<>();
            if (data != null) {
                String[] strings = data.split(",");
                for (String string : strings) {
                    prefixes.add(string.trim());
                }
            }
            return prefixes;
        }
    }


}
