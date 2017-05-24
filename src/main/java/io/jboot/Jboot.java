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
import io.jboot.event.JbootEvent;
import io.jboot.event.JbootEventManager;
import io.jboot.http.JbootHttp;
import io.jboot.http.JbootHttpManager;
import io.jboot.mq.Jbootmq;
import io.jboot.mq.JbootmqManager;
import io.jboot.rpc.Jbootrpc;
import io.jboot.rpc.JbootrpcManager;
import io.jboot.server.AutoDeployManager;
import io.jboot.server.JbootServer;
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

        argMap = new HashMap<>();
        for (String arg : args) {
            int indexOf = arg.indexOf("=");
            if (arg.startsWith("--") && indexOf > 0) {
                String key = arg.substring(2, indexOf);
                String value = arg.substring(indexOf + 1);
                argMap.put(key, value);
            }
        }
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

        JbootServerFactory factory = JbootServerFactory.me();
        JbootServer jBootServer = factory.buildServer();
        if (jBootServer.start()) {
            System.out.println(jBootServer.getConfig());
            JbootrpcManager.me().autoExport();

            if (isDevMode()) {
                AutoDeployManager.me().run();
            }

        } else {
            System.out.println(jBootServer.getConfig());
            System.err.println("jboot start fail!!!");
        }

        printJbootConfigInfo();
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


    public static Jbootrpc getJbootrpc() {
        if (jbootrpc == null) {
            jbootrpc = JbootrpcManager.me().getJbootrpc();
        }
        return jbootrpc;
    }


    public static <T> T service(Class<T> clazz) {
        return service(clazz, "jboot", "1.0");
    }

    public static <T> T service(Class<T> clazz, String group, String version) {
        return getJbootrpc().serviceObtain(clazz, "jboot", "1.0");
    }

    public static void sendEvent(JbootEvent event) {
        JbootEventManager.me().pulish(event);
    }

    public static void sendEvent(String action, Object data) {
        sendEvent(new JbootEvent(action, data));
    }

    public static <T> Jbootmq<T> getJbootmq(Class<T> clazz) {
        return JbootmqManager.me().getJbootmq(clazz);
    }

    public static JbootCache getJbootCache() {
        if (jbootCache == null) {
            jbootCache = JbootCacheManager.me().getCache();
        }
        return jbootCache;
    }

    public static JbootHttp getJbootHttp() {
        return JbootHttpManager.me().getJbootHttp();
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
