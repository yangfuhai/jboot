/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

public class ClassScanner {

    private static final Set<Class<?>> appClassesCache = new HashSet<>();

    public static final Set<String> scanJars = new HashSet<>();
    public static final Set<String> excludeJars = new HashSet<>();

    public static final Set<String> scanClasses = new HashSet<>();
    public static final Set<String> excludeClasses = new HashSet<>();

    // dev模式打开扫描信息打印
    private static boolean printScannerInfoEnable = false;

    public static boolean isPrintScannerInfoEnable() {
        return printScannerInfoEnable;
    }

    public static void setPrintScannerInfoEnable(boolean printScannerInfoEnable) {
        ClassScanner.printScannerInfoEnable = printScannerInfoEnable;
    }


    public static void addScanJarPrefix(String prefix) {
        scanJars.add(prefix.toLowerCase().trim());
    }

    static {
        scanJars.add("jboot");
    }


    public static void addUnscanJarPrefix(String prefix) {
        excludeJars.add(prefix.toLowerCase().trim());
    }

    static {
        excludeJars.add("jfinal-");
        excludeJars.add("cos-");
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
        excludeJars.add("junit5-");
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
        excludeJars.add("commons-compress");
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
        excludeJars.add("protostuff-");
        excludeJars.add("poi-");
        excludeJars.add("easypoi-");
        excludeJars.add("ognl-");
        excludeJars.add("xmlbeans-");
        excludeJars.add("master-slave-core-");
        excludeJars.add("shadow-core-rewrite-");
        excludeJars.add("apiguardian-api-");
        excludeJars.add("opentest4j-");
        excludeJars.add("opentracing-");
        excludeJars.add("freemarker-");
        excludeJars.add("protobuf-");
        excludeJars.add("jdom2-");
        excludeJars.add("useragentutils-");
        excludeJars.add("common-io-");
        excludeJars.add("common-image-");
        excludeJars.add("common-lang-");
        excludeJars.add("imageio-");
        excludeJars.add("curvesapi-");
        excludeJars.add("myexcel-");
        excludeJars.add("oshi-");
        excludeJars.add("classmate-");
        excludeJars.add("hibernate-");
        excludeJars.add("aspectjweaver-");
        excludeJars.add("aspectjrt-");
        excludeJars.add("simpleclient_");
        excludeJars.add("rocketmq-");
        excludeJars.add("clickhouse-");
        excludeJars.add("lz4-");
        excludeJars.add("commons-digester-");
        excludeJars.add("opencc4j-");
        excludeJars.add("heaven-");
        excludeJars.add("tinypinyin-");
        excludeJars.add("jieba-");
        excludeJars.add("ahocorasick-");
        excludeJars.add("kotlin-");
        excludeJars.add("xml-apis-");
        excludeJars.add("dom4j-");
        excludeJars.add("ini4j-");
        excludeJars.add("cache-api-");
        excludeJars.add("byte-buddy-");
        excludeJars.add("jodd-");
        excludeJars.add("redisson-");
        excludeJars.add("bcprov-");
        excludeJars.add("pay-java-");
        excludeJars.add("alipay-sdk-");
        excludeJars.add("mapper-extras-");
        excludeJars.add("org.jacoco");
        excludeJars.add("jxl-");
        excludeJars.add("jxls-");
        excludeJars.add("jstl-");
        excludeJars.add("batik-");
        excludeJars.add("xmlsec-");
        excludeJars.add("pdfbox-");
        excludeJars.add("fontbox-");
        excludeJars.add("xk-time-");
        excludeJars.add("geohash-");
        excludeJars.add("ezmorph-");
        excludeJars.add("async-http-");
        excludeJars.add("jsr-");
        excludeJars.add("jsr250");
        excludeJars.add("pinyin4j");
        excludeJars.add("ijpay-");
        excludeJars.add("wildfly-");
        excludeJars.add("liquibase-");
        excludeJars.add("flowable-");
        excludeJars.add("mybatis-");
        excludeJars.add("ip2region-");
        excludeJars.add("java-uuid-generator-");
        excludeJars.add("quartz-");
        excludeJars.add("elasticjob-");
        excludeJars.add("reflections-");
        excludeJars.add("jts-");
        excludeJars.add("json-");
        excludeJars.add("httpclient5-");
        excludeJars.add("httpcore5-");
        excludeJars.add("jul-to-");
        excludeJars.add("calcite-");
        excludeJars.add("avatica-");
        excludeJars.add("encoder-");
        excludeJars.add("aggdesigner-");
        excludeJars.add("uzaygezen-");
        excludeJars.add("memory-");
        excludeJars.add("commons-");
        excludeJars.add("accessors-");
        excludeJars.add("sketches-");
        excludeJars.add("h2-");
        excludeJars.add("cosid-");
        excludeJars.add("mchange-");
        excludeJars.add("janino-");
        excludeJars.add("jnanoid-");
        excludeJars.add("proj4j-");
        excludeJars.add("sparsebitset-");
        excludeJars.add("captcha-");
        excludeJars.add("cryptokit");
        excludeJars.add("isec-");
        excludeJars.add("jurt-");
        excludeJars.add("minio-");
        excludeJars.add("logging-");
        excludeJars.add("simple-xml-");
        excludeJars.add("jodconverter-");
        excludeJars.add("credentials-");
        excludeJars.add("unoil-");
        excludeJars.add("endpoint-");
        excludeJars.add("ridl-");
        excludeJars.add("tencentcloud-");
        excludeJars.add("yauaa-");
        excludeJars.add("tea-");
        excludeJars.add("fr.");
        excludeJars.add("vod20");
        excludeJars.add("juh-");
        excludeJars.add("prefixmap-");
        excludeJars.add("dmjdbcdriver");
    }


    public static void addUnscanClassPrefix(String prefix) {
        excludeClasses.add(prefix.trim());
    }

    static {
        excludeClasses.add("java.");
        excludeClasses.add("javax.");
        excludeClasses.add("junit.");
        excludeClasses.add("jline.");
        excludeClasses.add("redis.");
        excludeClasses.add("lombok.");
        excludeClasses.add("oshi.");
        excludeClasses.add("jodd.");
        excludeClasses.add("javassist.");
        excludeClasses.add("google.");
        excludeClasses.add("com.jfinal.");
        excludeClasses.add("com.aliyuncs.");
        excludeClasses.add("com.carrotsearch.");
        excludeClasses.add("org.aopalliance.");
        excludeClasses.add("org.apache.");
        excludeClasses.add("org.nustaq.");
        excludeClasses.add("net.sf.");
        excludeClasses.add("org.slf4j.");
        excludeClasses.add("org.antlr.");
        excludeClasses.add("org.jboss.");
        excludeClasses.add("org.checkerframework.");
        excludeClasses.add("org.jsoup.");
        excludeClasses.add("org.objenesis.");
        excludeClasses.add("org.ow2.");
        excludeClasses.add("org.reactivest.");
        excludeClasses.add("org.yaml.");
        excludeClasses.add("org.checker");
        excludeClasses.add("org.codehaus");
        excludeClasses.add("org.commonmark");
        excludeClasses.add("org.jdom2.");
        excludeClasses.add("org.aspectj.");
        excludeClasses.add("org.hibernate.");
        excludeClasses.add("org.ahocorasick.");
        excludeClasses.add("org.lionsoul.jcseg.");
        excludeClasses.add("org.ini4j.");
        excludeClasses.add("org.jetbrains.");
        excludeClasses.add("org.jacoco.");
        excludeClasses.add("org.xnio.");
        excludeClasses.add("org.bouncycastle.");
        excludeClasses.add("org.elasticsearch.");
        excludeClasses.add("org.hamcrest.");
        excludeClasses.add("org.objectweb.");
        excludeClasses.add("org.joda.");
        excludeClasses.add("org.wildfly.");
        excludeClasses.add("org.owasp.");
        excludeClasses.add("aj.org.");
        excludeClasses.add("ch.qos.");
        excludeClasses.add("joptsimple.");
        excludeClasses.add("com.alibaba.csp.");
        excludeClasses.add("com.alibaba.nacos.");
        excludeClasses.add("com.alibaba.druid.");
        excludeClasses.add("com.alibaba.fastjson.");
        excludeClasses.add("com.aliyun.open");
        excludeClasses.add("com.caucho");
        excludeClasses.add("com.codahale");
        excludeClasses.add("com.ctrip.framework.apollo");
        excludeClasses.add("com.ecwid.");
        excludeClasses.add("com.esotericsoftware.");
        excludeClasses.add("com.fasterxml.");
        excludeClasses.add("com.github.");
        excludeClasses.add("io.github.");
        excludeClasses.add("com.google.");
        excludeClasses.add("metrics_influxdb.");
        excludeClasses.add("com.rabbitmq.");
        excludeClasses.add("com.squareup.");
        excludeClasses.add("com.sun.");
        excludeClasses.add("com.typesafe.");
        excludeClasses.add("com.weibo.api.motan.");
        excludeClasses.add("com.zaxxer.");
        excludeClasses.add("com.mysql.");
        excludeClasses.add("com.papertrail.");
        excludeClasses.add("com.egzosn.");
        excludeClasses.add("com.alipay.api");
        excludeClasses.add("org.gjt.");
        excludeClasses.add("org.fusesource.");
        excludeClasses.add("org.redisson.");
        excludeClasses.add("io.dropwizard");
        excludeClasses.add("io.prometheus");
        excludeClasses.add("io.jsonwebtoken");
        excludeClasses.add("io.lettuce");
        excludeClasses.add("reactor.adapter");
        excludeClasses.add("io.seata.");
        excludeClasses.add("io.swagger.");
        excludeClasses.add("io.undertow.");
        excludeClasses.add("io.netty.");
        excludeClasses.add("io.opentracing.");
        excludeClasses.add("it.sauronsoftware");
        excludeClasses.add("net.oschina.j2cache");
        excludeClasses.add("net.bytebuddy");
        excludeClasses.add("cn.hutool.");
        excludeClasses.add("com.dyuproject.");
        excludeClasses.add("io.protostuff.");
        excludeClasses.add("io.reactivex.");
        excludeClasses.add("freemarker.");
        excludeClasses.add("com.twelvemonkeys.");
        excludeClasses.add("eu.bitwalker.");
        excludeClasses.add("jstl.");
        excludeClasses.add("jxl.");
        excludeClasses.add("org.jxls");
        excludeClasses.add("org.kordamp");
        excludeClasses.add("org.mybatis");
        excludeClasses.add("org.lisonsoul");
        excludeClasses.add("org.flowable");
    }


    public static void addScanClassPrefix(String prefix) {
        scanClasses.add(prefix.toLowerCase().trim());
    }

    static {
        scanClasses.add("io.jboot.support.shiro.directives");
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

        String unScanClassPrefix = JbootConfigManager.me().getConfigValue("jboot.app.scanner.unScanClassPrefix");
        if (unScanClassPrefix != null) {
            String[] prefixes = unScanClassPrefix.split(",");
            for (String prefix : prefixes) {
                if (prefix != null && prefix.trim().length() > 0) {
                    addUnscanClassPrefix(prefix.trim());
                }
            }
        }

        String scanClassPrefix = JbootConfigManager.me().getConfigValue("jboot.app.scanner.scanClassPrefix");
        if (scanClassPrefix != null) {
            String[] prefixes = scanClassPrefix.split(",");
            for (String prefix : prefixes) {
                if (prefix != null && prefix.trim().length() > 0) {
                    addScanClassPrefix(prefix.trim());
                }
            }
        }

    }

    public static <T> List<Class<T>> scanSubClass(Class<T> pclazz) {
        return scanSubClass(pclazz, false);
    }


    public static <T> List<Class<T>> scanSubClass(Class<T> pclazz, boolean instantiable) {
        initIfNecessary();
        List<Class<T>> classes = new ArrayList<>();
        findChildClasses(classes, pclazz, instantiable);
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

        return scanClass(ClassScanner::isInstantiable);

    }

    public static List<Class> scanClass(Predicate<Class> filter) {

        initIfNecessary();

        return appClassesCache.stream()
                .filter(filter)
                .collect(Collectors.toList());

    }

    public static void clearAppClassesCache() {
        appClassesCache.clear();
    }


    private static boolean isInstantiable(Class clazz) {
        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
    }


    public static List<Class> scanClassByAnnotation(Class annotationClass, boolean instantiable) {
        initIfNecessary();

        List<Class> list = new ArrayList<>();
        for (Class clazz : appClassesCache) {
            Annotation annotation = clazz.getAnnotation(annotationClass);
            if (annotation == null) {
                continue;
            }

            if (instantiable && !isInstantiable(clazz)) {
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


    private static <T> void findChildClasses(List<Class<T>> classes, Class<T> parent, boolean instantiable) {
        for (Class clazz : appClassesCache) {

            if (!parent.isAssignableFrom(clazz)) {
                continue;
            }

            if (instantiable && !isInstantiable(clazz)) {
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
                        .getParentFile().getAbsolutePath().replace('\\', '/');
                continue;
            }

            if (isPrintScannerInfoEnable()) {
                System.out.println("Jboot Scan ClassPath: " + classPath);
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

            if (isPrintScannerInfoEnable()) {
                System.out.println("Jboot Scan Jar: " + jarPath);
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
                if (jarEntry.isDirectory() && entryName.startsWith("BOOT-INF/classes/")) {

                    if (isPrintScannerInfoEnable()) {
                        System.out.println("Jboot Scan entryName: " + entryName);
                    }

                    if (entryName.endsWith(".class")) {
                        String className = entryName.replace("/", ".").substring(0, entryName.length() - 6);
                        addClass(classForName(className));
                    }
                } else {
                    if (entryName.endsWith(".class")) {
                        String className = entryName.replace("/", ".").substring(0, entryName.length() - 6);
                        addClass(classForName(className));
                    } else if (entryName.startsWith("BOOT-INF/lib/") && entryName.endsWith(".jar")) {
                        if (!isIncludeJar(entryName)) {
                            continue;
                        }

                        if (isPrintScannerInfoEnable()) {
                            System.out.println("Jboot Scan Jar: " + entryName);
                        }

                        try (JarInputStream jarStream = new JarInputStream(jarFile
                                .getInputStream(jarEntry));) {
                            JarEntry innerEntry = jarStream.getNextJarEntry();
                            while (innerEntry != null) {
                                if (!innerEntry.isDirectory()) {
                                    String nestedEntryName = innerEntry.getName();
                                    if (nestedEntryName.endsWith(".class")) {
                                        String className = nestedEntryName.replace("/", ".").substring(0, nestedEntryName.length() - 6);
                                        addClass(classForName(className));
                                    }
                                }
                                innerEntry = jarStream.getNextJarEntry();
                            }
                        }
                    }
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
        for (String prefix : scanClasses) {
            if (clazzName.startsWith(prefix)) {
                return true;
            }
        }
        for (String prefix : excludeClasses) {
            if (clazzName.startsWith(prefix)) {
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
                        if (path.toLowerCase().endsWith("!/") || path.toLowerCase().endsWith("!")) {
                        } else {
                            classPaths.add(new File(path).getCanonicalPath().replace('\\', '/'));
                        }
                    } else {
                        jarPaths.add(new File(path).getCanonicalPath().replace('\\', '/'));
                    }
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
            try {
                if (!path.toLowerCase().endsWith(".jar") && !jarPaths.contains(path)) {
                    if (path.toLowerCase().endsWith("!/") || path.toLowerCase().endsWith("!")) {
                    } else {
                        classPaths.add(new File(path).getCanonicalPath().replace('\\', '/'));
                    }
                } else {
                    jarPaths.add(new File(path).getCanonicalPath().replace('\\', '/'));
                }
            } catch (IOException e) {
            }
        }
    }


    private static boolean isIncludeJar(String path) {

        String jarName = new File(path).getName().toLowerCase();

        for (String include : scanJars) {
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
        if (path.contains("/jre/lib")) {
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
                String javaHomeString = System.getProperty("java.home");
                if (javaHomeString != null && javaHomeString.trim().length() > 0) {
                    javaHome = new File(javaHomeString, "..").getCanonicalPath().replace('\\', '/');
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return javaHome;
    }

}
