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

import com.jfinal.kit.PathKit;
import io.jboot.cache.JbootCache;
import io.jboot.cache.JbootCacheManager;
import io.jboot.config.JbootProperties;
import io.jboot.core.redis.JbootRedis;
import io.jboot.event.JbootEvent;
import io.jboot.event.JbootEventManager;
import io.jboot.http.JbootHttp;
import io.jboot.http.JbootHttpManager;
import io.jboot.http.JbootHttpRequest;
import io.jboot.http.JbootHttpResponse;
import io.jboot.mq.Jbootmq;
import io.jboot.mq.JbootmqManager;
import io.jboot.rpc.Jbootrpc;
import io.jboot.rpc.JbootrpcManager;
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

    public static void setBootArg(String key, String value) {
        if (argMap == null) {
            argMap = new HashMap<>();
        }
        argMap.put(key, value);
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


        System.err.println("jboot start success!!!");

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
            jbootRedis = new JbootRedis();
        }
        return jbootRedis;
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
