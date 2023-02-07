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
package io.jboot;

import com.codahale.metrics.MetricRegistry;
import com.jfinal.aop.Aop;
import io.jboot.aop.JbootAopFactory;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.components.cache.JbootCache;
import io.jboot.components.cache.JbootCacheManager;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventManager;
import io.jboot.components.mq.Jbootmq;
import io.jboot.components.mq.JbootmqManager;
import io.jboot.components.rpc.JbootrpcManager;
import io.jboot.components.rpc.JbootrpcReferenceConfig;
import io.jboot.components.serializer.JbootSerializer;
import io.jboot.components.serializer.JbootSerializerManager;
import io.jboot.support.metric.JbootMetricManager;
import io.jboot.support.redis.JbootRedis;
import io.jboot.support.redis.JbootRedisManager;
import io.jboot.utils.StrUtil;


public class Jboot {

    /**
     * 是否是开发模式
     *
     * @return
     */
    public static boolean isDevMode() {
        return JbootConfigManager.me().isDevMode();
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
     * 获取指定的缓存
     *
     * @param name
     * @return
     */
    public static JbootCache getCache(String name) {
        return JbootCacheManager.me().getCache(name);
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
     * 获取指定的 Redis
     *
     * @param name
     * @return
     */
    public static JbootRedis getRedis(String name) {
        return JbootRedisManager.me().getRedis(name);
    }


    /**
     * 获取 MetricRegistry
     *
     * @return
     */
    public static MetricRegistry getMetric() {
        return JbootMetricManager.me().metric();
    }


    /**
     * 获取 Mq
     *
     * @return
     */
    public static Jbootmq getMq() {
        return JbootmqManager.me().getJbootmq();
    }

    /**
     * 获取指定的 MQ
     *
     * @param name
     * @return
     */
    public static Jbootmq getMq(String name) {
        return JbootmqManager.me().getJbootmq(name);
    }


    /**
     * 获取序列化对象
     *
     * @return
     */
    public static JbootSerializer getSerializer() {
        return JbootSerializerManager.me().getSerializer();
    }


    /**
     * 获取序列化对象
     * @param name
     * @return
     */
    public static JbootSerializer getSerializer(String name) {
        return JbootSerializerManager.me().getSerializer(name);
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
     * 读取某个配置信息
     *
     * @param key
     * @return
     */
    public static String configValue(String key) {
        return JbootConfigManager.me().getConfigValue(key);
    }


    /**
     * 读取某个配置信息
     *
     * @param key
     * @param defaultValue 当获取不到的时候发挥此默认值
     * @return
     */
    public static String configValue(String key, String defaultValue) {
        String value = configValue(key);
        return StrUtil.isNotBlank(value) ? value : defaultValue;
    }


    /**
     * 获取 RPC 服务
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T service(Class<T> clazz) {
        return service(clazz, new JbootrpcReferenceConfig());
    }

    /**
     * 获取 RPC 服务
     *
     * @param clazz
     * @param config rpc 配置
     * @param <T>
     * @return
     */
    public static <T> T service(Class<T> clazz, JbootrpcReferenceConfig config) {
        return JbootrpcManager.me().getJbootrpc().serviceObtain(clazz, config);
    }

    /**
     * 想本地系统发送一个事件
     *
     * @param event
     */
    public static void sendEvent(JbootEvent event) {
        JbootEventManager.me().publish(event);
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
     * 根据类名获取 Aop 下的 Bean
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return Aop.get(clazz);
    }

    /**
     * 根据名称获取 Aop 下的 Bean
     *
     * @param name
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name) {
        return JbootAopFactory.me().getBean(name);
    }

}
