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
package io.jboot;

import com.codahale.metrics.MetricRegistry;
import com.jfinal.kit.PathKit;
import io.jboot.aop.JbootInjectManager;
import io.jboot.app.listener.JbootAppListenerManager;
import io.jboot.component.metric.JbootMetricManager;
import io.jboot.component.redis.JbootRedis;
import io.jboot.component.redis.JbootRedisManager;
import io.jboot.config.JbootConfigManager;
import io.jboot.core.cache.JbootCache;
import io.jboot.core.cache.JbootCacheManager;
import io.jboot.core.http.JbootHttp;
import io.jboot.core.http.JbootHttpManager;
import io.jboot.core.mq.Jbootmq;
import io.jboot.core.mq.JbootmqManager;
import io.jboot.core.rpc.Jbootrpc;
import io.jboot.core.rpc.JbootrpcConfig;
import io.jboot.core.rpc.JbootrpcManager;
import io.jboot.core.rpc.JbootrpcServiceConfig;
import io.jboot.core.serializer.ISerializer;
import io.jboot.core.serializer.SerializerManager;
import io.jboot.event.JbootEvent;
import io.jboot.event.JbootEventManager;
import io.jboot.utils.FileUtils;
import io.jboot.utils.StrUtils;
import io.jboot.web.JbootWebConfig;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * JBOOT 启动类，项目入口
 */
public class Jboot {


    private static Map<String, String> argMap;


    private JbootConfig jbootConfig;
    private Boolean devMode;
    private Jbootrpc jbootrpc;
    private JbootCache jbootCache;
    private JbootHttp jbootHttp;
    private JbootRedis jbootRedis;


    private static Jboot jboot = new Jboot();

    public static Jboot me() {
        return jboot;
    }

    /**
     * main 入口方法
     *
     * @param args
     */
    public static void main(String[] args) {
        run(args);
    }


    public static void run(String[] args) {
        parseArgs(args);
        jboot.start();
    }


    /**
     * 解析启动参数
     *
     * @param args
     */
    private static void parseArgs(String[] args) {
        if (args == null || args.length == 0) {
            return;
        }

        for (String arg : args) {
            int indexOf = arg.indexOf("=");
            if (arg.startsWith("--") && indexOf > 0) {
                String key = arg.substring(2, indexOf);
                String value = arg.substring(indexOf + 1);
                setBootArg(key, value);
            }
        }
    }

    public static void setBootArg(String key, Object value) {
        if (argMap == null) {
            argMap = new HashMap<>();
        }
        argMap.put(key, value.toString());
    }

    /**
     * 获取启动参数
     *
     * @param key
     * @return
     */
    public static String getBootArg(String key) {
        if (argMap == null) return null;
        return argMap.get(key);
    }

    public static Map<String, String> getBootArgs() {
        return argMap;
    }


    /**
     * 开始启动
     */
    public void start() {

        printBannerInfo();
        printConfigInfo();

//        ensureServerCreated();
//
//        if (!startServer()) {
//            System.err.println("jboot start fail!!!");
//            return;
//        }

        printServerPath();
        printServerUrl();

        JbootAppListenerManager.me().onJbootStarted();

    }



    private void printBannerInfo() {
        System.out.println(getBannerText());
    }

    private String getBannerText() {
        JbootConfig config = getJbootConfig();

        if (!config.isBannerEnable()) {
            return "";
        }

        File bannerFile = new File(getRootClassPath(), config.getBannerFile());
        if (bannerFile.exists() && bannerFile.canRead()) {
            String bannerFileText = FileUtils.readString(bannerFile);
            if (StrUtils.isNotBlank(bannerFileText)) {
                return bannerFileText;
            }
        }

        return "  ____  ____    ___    ___   ______ \n" +
                " |    ||    \\  /   \\  /   \\ |      |\n" +
                " |__  ||  o  )|     ||     ||      |\n" +
                " __|  ||     ||  O  ||  O  ||_|  |_|\n" +
                "/  |  ||  O  ||     ||     |  |  |  \n" +
                "\\  `  ||     ||     ||     |  |  |  \n" +
                " \\____||_____| \\___/  \\___/   |__|  \n" +
                "                                    ";

    }

    private void printConfigInfo() {
        System.out.println(getJbootConfig());
        System.out.println(config(JbootWebConfig.class));
    }


    private void printServerPath() {
        System.out.println("server classPath : " + getRootClassPath());
        System.out.println("server webRoot : " + PathKit.getWebRootPath());
    }


    private void printServerUrl() {
//        JbootServerConfig serverConfig = config(JbootServerConfig.class);
//
//        String host = "0.0.0.0".equals(serverConfig.getHost()) ? "127.0.0.1" : serverConfig.getHost();
//        String port = "80".equals(serverConfig.getPort()) ? "" : ":" + serverConfig.getPort();
//        String path = serverConfig.getContextPath();
//
//        String url = String.format("http://%s%s%s", host, port, path);
//
//        System.out.println("server started success , url : " + url);
    }

    private static String getRootClassPath() {
        String path = null;
        try {
            path = Jboot.class.getClassLoader().getResource("").toURI().getPath();
            return new File(path).getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return path;
    }


    ///////////get component methods///////////


    /**
     * 是否是开发模式
     *
     * @return
     */
    public boolean isDevMode() {
        if (devMode == null) {
            JbootConfig config = getJbootConfig();
            devMode = MODE.DEV.getValue().equals(config.getMode());
        }
        return devMode;
    }

    /**
     * 获取JbootConfig 配置文件
     *
     * @return
     */
    public JbootConfig getJbootConfig() {
        if (jbootConfig == null) {
            jbootConfig = config(JbootConfig.class);
        }
        return jbootConfig;
    }


    /**
     * 获取 Jbootrpc，进行服务获取和发布
     *
     * @return
     */
    public Jbootrpc getRpc() {
        if (jbootrpc == null) {
            jbootrpc = JbootrpcManager.me().getJbootrpc();
        }
        return jbootrpc;
    }


    /**
     * 获取 MQ，进行消息发送
     *
     * @return
     */
    public Jbootmq getMq() {
        return JbootmqManager.me().getJbootmq();
    }

    /**
     * 获取 缓存
     *
     * @return
     */
    public JbootCache getCache() {
        if (jbootCache == null) {
            jbootCache = JbootCacheManager.me().getCache();
        }
        return jbootCache;
    }

    /**
     * 获取 jbootHttp 工具类，方便操作http请求
     *
     * @return
     */
    public JbootHttp getHttp() {
        if (jbootHttp == null) {
            jbootHttp = JbootHttpManager.me().getJbootHttp();
        }
        return jbootHttp;
    }


    /**
     * 获取 JbootRedis 工具类，方便操作Redis请求
     *
     * @return
     */
    public JbootRedis getRedis() {
        if (jbootRedis == null) {
            jbootRedis = JbootRedisManager.me().getRedis();
        }
        return jbootRedis;
    }


    /**
     * 获取 MetricRegistry
     *
     * @return
     */
    public MetricRegistry getMetric() {
        return JbootMetricManager.me().metric();
    }


    /**
     * 获取序列化对象
     *
     * @return
     */
    public ISerializer getSerializer() {
        return SerializerManager.me().getSerializer();
    }


    ////////// static tool methods///////////

    /**
     * 获取配置信息
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T config(Class<T> clazz) {
        return JbootConfigManager.me().get(clazz);
    }

    /**
     * 读取配置文件信息
     *
     * @param clazz
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T config(Class<T> clazz, String prefix) {
        return JbootConfigManager.me().get(clazz, prefix, null);
    }


    /**
     * 读取配置文件信息
     *
     * @param clazz
     * @param prefix
     * @param file
     * @param <T>
     * @return
     */
    public static <T> T config(Class<T> clazz, String prefix, String file) {
        return JbootConfigManager.me().get(clazz, prefix, file);
    }


    private JbootrpcConfig rpcConfig;

    /**
     * 获取 RPC 服务
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T service(Class<T> clazz) {
        return service(clazz, new JbootrpcServiceConfig());
    }

    /**
     * 获取 RPC 服务
     *
     * @param clazz
     * @param config rpc 配置
     * @param <T>
     * @return
     */
    public static <T> T service(Class<T> clazz, JbootrpcServiceConfig config) {
        return jboot.getRpc().serviceObtain(clazz, config);
    }

    /**
     * 想本地系统发送一个事件
     *
     * @param event
     */
    public static void sendEvent(JbootEvent event) {
        JbootEventManager.me().pulish(event);
    }

    /**
     * 向本地系统发送一个事件
     *
     * @param action
     * @param data
     */
    public static void sendEvent(String action, Object data) {
        sendEvent(new JbootEvent(action, data));
    }



    /**
     * 获取被增强的，可以使用AOP注入的实体类
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T bean(Class<T> clazz) {
//        return JbootInjectManager.me().getInjector().getInstance(clazz);
        return JbootInjectManager.me().getInstance(clazz);
    }


    /**
     * 对某个对象内部的变量进行注入
     *
     * @param object
     */
    public static void injectMembers(Object object) {
        JbootInjectManager.me().injectMembers(object);
    }



    /**
     * 产品模式：开发、测试、产品
     */
    public static enum MODE {

        DEV("dev"), TEST("test"), PRODUCT("product");

        private final String value;

        MODE(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
