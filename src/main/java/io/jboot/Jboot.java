/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Injector;
import com.jfinal.kit.PathKit;
import io.jboot.aop.JbootInjectManager;
import io.jboot.component.redis.JbootRedisManager;
import io.jboot.core.cache.JbootCache;
import io.jboot.core.cache.JbootCacheManager;
import io.jboot.config.JbootProperties;
import io.jboot.component.metrics.JbootMetricsManager;
import io.jboot.component.redis.JbootRedis;
import io.jboot.core.serializer.ISerializer;
import io.jboot.core.serializer.SerializerManager;
import io.jboot.event.JbootEvent;
import io.jboot.event.JbootEventManager;
import io.jboot.core.http.JbootHttp;
import io.jboot.core.http.JbootHttpManager;
import io.jboot.core.http.JbootHttpRequest;
import io.jboot.core.http.JbootHttpResponse;
import io.jboot.core.mq.Jbootmq;
import io.jboot.core.mq.JbootmqManager;
import io.jboot.core.rpc.Jbootrpc;
import io.jboot.core.rpc.JbootrpcManager;
import io.jboot.server.AutoDeployManager;
import io.jboot.server.JbootServer;
import io.jboot.server.JbootServerConfig;
import io.jboot.server.JbootServerFactory;
import io.jboot.utils.FileUtils;
import io.jboot.utils.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * JBOOT 启动类，项目入口
 */
public class Jboot {

    public static final String EVENT_STARTED = "jboot:started";

    private static JbootConfig jbootConfig;
    private static Boolean devMode;
    private static Map<String, String> argMap;

    private static Jbootrpc jbootrpc;
    private static JbootCache jbootCache;
    private static JbootHttp jbootHttp;
    private static JbootRedis jbootRedis;

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
        start();
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


    /**
     * 开始启动
     */
    public static void start() {

        printBannerInfo();
        printJbootConfigInfo();
        printServerConfigInfo();


        JbootServerFactory factory = JbootServerFactory.me();
        JbootServer jbootServer = factory.buildServer();


        boolean startSuccess = jbootServer.start();

        if (!startSuccess) {
            System.err.println("jboot start fail!!!");
            return;
        }

        printServerPath();
        printServerUrl();

        JbootrpcManager.me().autoExport();

        if (isDevMode()) {
            AutoDeployManager.me().run();
        }

    }

    private static void printBannerInfo() {
        JbootConfig config = getJbootConfig();

        if (!config.isBannerEnable()) {
            return;
        }

        File bannerFile = new File(PathKit.getRootClassPath(), config.getBannerFile());

        String bannerText = "  ____  ____    ___    ___   ______ \n" +
                " |    ||    \\  /   \\  /   \\ |      |\n" +
                " |__  ||  o  )|     ||     ||      |\n" +
                " __|  ||     ||  O  ||  O  ||_|  |_|\n" +
                "/  |  ||  O  ||     ||     |  |  |  \n" +
                "\\  `  ||     ||     ||     |  |  |  \n" +
                " \\____||_____| \\___/  \\___/   |__|  \n" +
                "                                    ";

        if (bannerFile.exists() && bannerFile.canRead()) {
            String bannerFileText = FileUtils.readString(bannerFile);
            bannerText = StringUtils.isNotBlank(bannerFileText) ? bannerFileText : bannerText;
        }

        System.out.println(bannerText);

    }

    private static void printJbootConfigInfo() {
        System.out.println(getJbootConfig());
    }

    private static void printServerConfigInfo() {
        System.out.println(config(JbootServerConfig.class));
    }

    private static void printServerPath() {
        System.out.println("server webRoot      : " + PathKit.getWebRootPath());
        System.out.println("server classPath    : " + PathKit.getRootClassPath());
    }


    private static void printServerUrl() {
        JbootServerConfig serverConfig = config(JbootServerConfig.class);

        String host = "0.0.0.0".equals(serverConfig.getHost()) ? "127.0.0.1" : serverConfig.getHost();
        String port = "80".equals(serverConfig.getPort()) ? "" : ":" + serverConfig.getPort();
        String path = serverConfig.getContextPath();

        String url = String.format("http://%s%s%s", host, port, path);

        System.out.println();
        System.out.println("server started success , url : " + url);
    }


    /**
     * 获取配置信息
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T config(Class<T> clazz) {
        return JbootProperties.get(clazz);
    }


    /**
     * 是否是开发模式
     *
     * @return
     */
    public static boolean isDevMode() {
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
    public static JbootConfig getJbootConfig() {
        if (jbootConfig == null) {
            jbootConfig = config(JbootConfig.class);
        }
        return jbootConfig;
    }


    public static <T> T service(Class<T> clazz) {
        return service(clazz, "jboot", "1.0");
    }

    public static <T> T service(Class<T> clazz, String group, String version) {
        return getRpc().serviceObtain(clazz, "jboot", "1.0");
    }

    public static void sendEvent(JbootEvent event) {
        JbootEventManager.me().pulish(event);
    }

    public static void sendEvent(String action, Object data) {
        sendEvent(new JbootEvent(action, data));
    }


    public static Jbootrpc getRpc() {
        if (jbootrpc == null) {
            jbootrpc = JbootrpcManager.me().getJbootrpc();
        }
        return jbootrpc;
    }


    public static Jbootmq getMq() {
        return JbootmqManager.me().getJbootmq();
    }

    /**
     * 获取 缓存
     *
     * @return
     */
    public static JbootCache getCache() {
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
    public static JbootHttp getHttp() {
        if (jbootHttp == null) {
            jbootHttp = JbootHttpManager.me().getJbootHttp();
        }
        return jbootHttp;
    }

    public static String httpGet(String url) {
        return httpGet(url, null);
    }

    public static String httpGet(String url, Map<String, Object> params) {
        JbootHttpRequest request = JbootHttpRequest.create(url, params, JbootHttpRequest.METHOD_GET);
        JbootHttpResponse response = getHttp().handle(request);
        return response.isError() ? null : response.getContent();
    }

    public static String httpPost(String url) {
        return httpPost(url, null);
    }

    public static String httpPost(String url, Map<String, Object> params) {
        JbootHttpRequest request = JbootHttpRequest.create(url, params, JbootHttpRequest.METHOD_POST);
        JbootHttpResponse response = getHttp().handle(request);
        return response.isError() ? null : response.getContent();
    }


    /**
     * 获取 JbootRedis 工具类，方便操作Redis请求
     *
     * @return
     */
    public static JbootRedis getRedis() {
        if (jbootRedis == null) {
            jbootRedis = JbootRedisManager.me().getReidis();
        }
        return jbootRedis;
    }


    /**
     * 获取 MetricRegistry
     *
     * @return
     */
    public static MetricRegistry getMetric() {
        return JbootMetricsManager.me().metric();
    }


    /**
     * 获取 injector
     *
     * @return
     */
    public static Injector getInjector() {
        return JbootInjectManager.me().getInjector();
    }


    public static ISerializer getSerializer() {
        return SerializerManager.me().getSerializer();
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
