/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import java.util.stream.Collectors;

public class ClassScanner {

    private static final Set<Class> appClassesCache = new HashSet<>();

    public static final Set<String> includeJars = new HashSet<>();
    public static final Set<String> excludeJars = new HashSet<>();
    public static final Set<String> excludeClasses = new HashSet<>();

    public static void addScanJarPrefix(String prefix) {
        includeJars.add(prefix.trim());
    }

    static {
        addScanJarPrefix("jboot");
    }

    public static void addUnscanJarPrefix(String prefix) {
        excludeJars.add(prefix.trim());
    }

    static {
        excludeJars.add("jfinal-");
        excludeJars.add("cos-20");
        excludeJars.add("cglib-");
        excludeJars.add("undertow-");
        excludeJars.add("xnio-");
        excludeJars.add("javax.");
        excludeJars.add("hikaricp-");
        excludeJars.add("druid-");
        excludeJars.add("mysql-");
        excludeJars.add("db2jcc-");
        excludeJars.add("db2jcc4-");
        excludeJars.add("ojdbc");
        excludeJars.add("junit-");
        excludeJars.add("org.junit");
        excludeJars.add("hamcrest-");
        excludeJars.add("jboss-");
        excludeJars.add("motan-");
        excludeJars.add("commons-pool");
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
        excludeJars.add("commons-cli");
        excludeJars.add("commons-math");
        excludeJars.add("commons-jxpath");
        excludeJars.add("audience-");
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
        excludeJars.add("httpmime-");
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
        excludeJars.add("st4-");
        excludeJars.add("icu4j-");
        excludeJars.add("idea_rt");
        excludeJars.add("mrjtoolkit");
        excludeJars.add("logback-");
        excludeJars.add("log4j-");
        excludeJars.add("log4j2-");
        excludeJars.add("aliyun-java-sdk-");
        excludeJars.add("aliyun-sdk-");
        excludeJars.add("archaius-");
        excludeJars.add("aopalliance-");
        excludeJars.add("hdrhistogram-");
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
        excludeJars.add("seata-");
        excludeJars.add("eureka-");
        excludeJars.add("netflix-");
        excludeJars.add("nacos-");
        excludeJars.add("apollo-");
        excludeJars.add("guice-");
        excludeJars.add("servlet-");
        excludeJars.add("debugger-agent.jar");
        excludeJars.add("xpp3_min-");
        excludeJars.add("latency");
        excludeJars.add("micrometer-");
        excludeJars.add("xstream-");
        excludeJars.add("jsr311-");
        excludeJars.add("servo-");
        excludeJars.add("compactmap-");
        excludeJars.add("dexx-");
        excludeJars.add("spotbugs-");
        excludeJars.add("xmlpull-");
        excludeJars.add("shardingsphere-");
        excludeJars.add("sentinel-");
        excludeJars.add("spring-");
        excludeJars.add("simpleclient-");
        excludeJars.add("breeze-");
        excludeJars.add("config-");
        excludeJars.add("encrypt-core-");
        excludeJars.add("lombok-");
        excludeJars.add("hutool-");
        excludeJars.add("jakarta.");
    }


    public static void addUnscanClass(String prefix) {
        excludeClasses.add(prefix.trim());
    }

    static {
        addUnscanClass("com.jfinal.");
        addUnscanClass("org.aopalliance.");
        addUnscanClass("org.apache.");
        addUnscanClass("org.nustaq.");
        addUnscanClass("net.sf.");
        addUnscanClass("org.slf4j.");
        addUnscanClass("org.antlr.");
        addUnscanClass("org.jboss.");
        addUnscanClass("org.javassist.");
        addUnscanClass("org.hamcrest.");
        addUnscanClass("org.jsoup.");
        addUnscanClass("org.objenesis.");
        addUnscanClass("org.ow2.");
        addUnscanClass("org.reactivest.");
        addUnscanClass("org.yaml.");
        addUnscanClass("org.checker");
        addUnscanClass("org.codehaus");
        addUnscanClass("ch.qos.");
        addUnscanClass("com.alibaba.csp.");
        addUnscanClass("com.alibaba.nacos.");
        addUnscanClass("com.alibaba.druid.");
        addUnscanClass("com.alibaba.fastjson.");
        addUnscanClass("com.aliyun.open");
        addUnscanClass("com.caucho");
        addUnscanClass("com.codahale");
        addUnscanClass("com.ctrip.framework.apollo");
        addUnscanClass("com.ecwid.");
        addUnscanClass("com.esotericsoftware.");
        addUnscanClass("com.fasterxml.");
        addUnscanClass("com.github.");
        addUnscanClass("com.google.");
        addUnscanClass("com.rabbitmq.");
        addUnscanClass("com.squareup.");
        addUnscanClass("com.typesafe.");
        addUnscanClass("com.weibo.");
        addUnscanClass("com.zaxxer.");
        addUnscanClass("com.mysql.");
        addUnscanClass("org.gjt.");
        addUnscanClass("io.dropwizard");
        addUnscanClass("io.jsonwebtoken");
        addUnscanClass("io.lettuce");
        addUnscanClass("reactor.adapter");
        addUnscanClass("io.prometheus");
        addUnscanClass("io.seata.");
        addUnscanClass("io.swagger.");
        addUnscanClass("io.undertow.");
        addUnscanClass("it.sauronsoftware");
        addUnscanClass("javax.");
        addUnscanClass("java.");
        addUnscanClass("junit.");
        addUnscanClass("jline.");
        addUnscanClass("redis.");
        addUnscanClass("lombok.");
        addUnscanClass("net.oschina.j2cache");
        addUnscanClass("cn.hutool.");
    }

    static {
        String scanJarPrefix = JbootConfigManager.me().getConfigValue("jboot.app.scanner.scanJarPrefix");
        if (scanJarPrefix != null) {
            String[] prefixes = scanJarPrefix.split(",");
            for (String prefix : prefixes) {
                if (prefix != null && prefix.trim().length() > 0) {
                    addScanJarPrefix(prefix.trim());
                }
            }
        }

        String unScanJarPrefix = JbootConfigManager.me().getConfigValue("jboot.app.scanner.unScanJarPrefix");
        if (unScanJarPrefix != null) {
            String[] prefixes = unScanJarPrefix.split(",");
            for (String prefix : prefixes) {
                if (prefix != null && prefix.trim().length() > 0) {
                    addUnscanJarPrefix(prefix.trim());
                }
            }
        }
    }

    public static <T> List<Class<T>> scanSubClass(Class<T> pclazz) {
        return scanSubClass(pclazz, false);
    }


    public static <T> List<Class<T>> scanSubClass(Class<T> pclazz, boolean isInstantiable) {
        initIfNecessary();
        List<Class<T>> classes = new ArrayList<>();
        findChildClasses(classes, pclazz, isInstantiable);
        return classes;
    }

    public static List<Class> scanClass() {
        return scanClass(false);
    }

    public static List<Class> scanClass(boolean isInstantiable) {

        initIfNecessary();

        if (!isInstantiable) {
            return new ArrayList<>(appClassesCache);
        }

        return appClassesCache.stream()
                .filter(ClassScanner::isInstantiable)
                .collect(Collectors.toList());

    }

    public static void clearAppClassesCache() {
        appClassesCache.clear();
    }


    private static boolean isInstantiable(Class clazz) {
        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
    }


    public static List<Class> scanClassByAnnotation(Class annotationClass, boolean isInstantiable) {
        initIfNecessary();

        List<Class> list = new ArrayList<>();
        for (Class clazz : appClassesCache) {
            Annotation annotation = clazz.getAnnotation(annotationClass);
            if (annotation == null) {
                continue;
            }

            if (isInstantiable && !isInstantiable(clazz)) {
                continue;
            }

            list.add(clazz);
        }

        return list;
    }

    private static void initIfNecessary() {
        if (appClassesCache.isEmpty()) {
            initAppClasses();
        }
    }


    private static <T> void findChildClasses(List<Class<T>> classes, Class<T> parent, boolean isInstantiable) {
        for (Class clazz : appClassesCache) {

            if (!parent.isAssignableFrom(clazz)) {
                continue;
            }

            if (isInstantiable && !isInstantiable(clazz)) {
                continue;
            }

            classes.add(clazz);
        }
    }


    private static void initAppClasses() {

        Set<String> jarPaths = new HashSet<>();
        Set<String> classPaths = new HashSet<>();

        // jdk8 及以下、
        // tomcat 容器、
        // jfinal-undertow、
        // 以上三种加载模式通过 classloader 获取
        findClassPathsAndJarsByClassloader(jarPaths, classPaths, ClassScanner.class.getClassLoader());

        //jdk9+ 等其他方式通过 classpath 获取
        findClassPathsAndJarsByClassPath(jarPaths, classPaths);


        String tomcatClassPath = null;

        for (String classPath : classPaths) {
            //过滤tomcat自身的lib 以及 bin 下的jar
            File tomcatApiJarFile = new File(classPath, "tomcat-api.jar");
            File tomcatJuliJarFile = new File(classPath, "tomcat-juli.jar");
            if (tomcatApiJarFile.exists() || tomcatJuliJarFile.exists()) {
                tomcatClassPath = tomcatApiJarFile
                        .getParentFile()
                        .getParentFile().getAbsolutePath();
                continue;
            }

            if (JbootConfigManager.me().isDevMode()) {
                System.out.println("ClassScanner scan classpath : " + classPath);
            }

            addClassesFromClassPath(classPath);
        }

        for (String jarPath : jarPaths) {

            //过滤 tomcat 的 jar，但是不能过滤 webapps 目录下的
            if (tomcatClassPath != null
                    && jarPath.startsWith(tomcatClassPath)
                    && !jarPath.contains("webapps")) {
                continue;
            }

            if (!isIncludeJar(jarPath)) {
                continue;
            }

            if (JbootConfigManager.me().isDevMode()) {
                System.out.println("ClassScanner scan jar : " + jarPath);
            }

            addClassesFromJar(jarPath);
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
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                }
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
        if (clazz != null && isNotExcludeClass(clazz.getName())) {
            appClassesCache.add(clazz);
        }
    }

    //用于在进行 fatjar 打包时，提高性能
    private static boolean isNotExcludeClass(String clazzName) {
        for (String Prefix : excludeClasses) {
            if (clazzName.startsWith(Prefix)) {
                return false;
            }
        }
        return true;
    }


    private static void findClassPathsAndJarsByClassloader(Set<String> jarPaths, Set<String> classPaths, ClassLoader classLoader) {
        try {
            URL[] urls = null;
            if (classLoader instanceof URLClassLoader) {
                URLClassLoader ucl = (URLClassLoader) classLoader;
                urls = ucl.getURLs();
            }
            if (urls != null) {
                for (URL url : urls) {
                    String path = url.getPath();
                    path = URLDecoder.decode(path, "UTF-8");

                    // path : /d:/xxx
                    if (path.startsWith("/") && path.indexOf(":") == 2) {
                        path = path.substring(1);
                    }

                    if (!path.toLowerCase().endsWith(".jar")) {
                        classPaths.add(new File(path).getCanonicalPath().replace('\\', '/'));
                        continue;
                    }

                    jarPaths.add(path.replace('\\', '/'));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ClassLoader parent = classLoader.getParent();
        if (parent != null) {
            findClassPathsAndJarsByClassloader(jarPaths, classPaths, parent);
        }
    }

    private static void findClassPathsAndJarsByClassPath(Set<String> jarPaths, Set<String> classPaths) {
        String classPath = System.getProperty("java.class.path");
        if (classPath == null || classPath.trim().length() == 0) {
            return;
        }
        String[] classPathArray = classPath.split(File.pathSeparator);
        if (classPathArray == null || classPathArray.length == 0) {
            return;
        }
        for (String path : classPathArray) {
            path = path.trim();

            if (path.startsWith("./")) {
                path = path.substring(2);
            }

            if (path.startsWith("/") && path.indexOf(":") == 2) {
                path = path.substring(1);
            }

            if (!path.toLowerCase().endsWith(".jar") && !jarPaths.contains(path)) {
                try {
                    classPaths.add(new File(path).getCanonicalPath().replace('\\', '/'));
                } catch (IOException e) {
                }
            } else {
                jarPaths.add(path.replace('\\', '/'));
            }
        }
    }


    private static boolean isIncludeJar(String path) {

        String jarName = new File(path).getName().toLowerCase();

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

        //from jre lib
        if (path.contains("/jre/lib")
                || path.contains("\\jre\\lib")) {
            return false;
        }

        //from java home
        if (getJavaHome() != null
                && path.startsWith(getJavaHome())) {
            return false;
        }

        return true;
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
        File[] files = new File(path).listFiles();
        if (null == files || files.length == 0) {
            return;
        }
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

}
