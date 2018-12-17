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
import com.jfinal.aop.Aop;
import io.jboot.core.cache.JbootCache;
import io.jboot.core.cache.JbootCacheManager;
import io.jboot.core.config.JbootConfigManager;
import io.jboot.core.event.JbootEvent;
import io.jboot.core.event.JbootEventManager;
import io.jboot.core.rpc.JbootrpcManager;
import io.jboot.core.rpc.JbootrpcServiceConfig;
import io.jboot.core.serializer.ISerializer;
import io.jboot.core.serializer.SerializerManager;
import io.jboot.support.metric.JbootMetricManager;
import io.jboot.support.redis.JbootRedis;
import io.jboot.support.redis.JbootRedisManager;

import java.util.HashMap;
import java.util.Map;

/**
 * JBOOT 启动类，项目入口
 */
public class Jboot {


    private static Map<String, String> argMap;


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
//        jboot.start();
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
     * 是否是开发模式
     *
     * @return
     */
    public static boolean isDevMode() {
//        if (devMode == null) {
//            JbootConfig config = getJbootConfig();
//            devMode = MODE.DEV.getValue().equals(config.getMode());
//        }
        return true;
    }


    /**
     * 获取 缓存
     *
     * @return
     */
    public static JbootCache getCache() {
        return JbootCacheManager.me().getCache();
    }



    /**
     * 获取 JbootRedis 工具类，方便操作Redis请求
     *
     * @return
     */
    public static JbootRedis getRedis() {
        return JbootRedisManager.me().getRedis();
    }


    /**
     * 获取 MetricRegistry
     *
     * @return
//     */
    public static MetricRegistry getMetric() {
        return JbootMetricManager.me().metric();
    }


    /**
     * 获取序列化对象
     *
     * @return
     */
    public static ISerializer getSerializer() {
        return SerializerManager.me().getSerializer();
    }


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
        return JbootrpcManager.me().getJbootrpc().serviceObtain(clazz, config);
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
        return Aop.get(clazz);
    }


    /**
     * 对某个对象内部的变量进行注入
     *
     * @param object
     */
    public static void injectMembers(Object object) {
        Aop.inject(object);
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
