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
package io.jboot.core.spi;

import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * SPI 扩展加载器
 * <p>
 * 使用方法：
 * <p>
 * 第一步：编写支持扩展点的类，例如MyJbootRpc extends Jbootrpc。
 * 第二步：给该类添加上注解 JbootSpi， 例如 @JbootSpi("myrpc") MyJbootRpc extends Jbootrpc ...
 * 第三步：给jboot.properties配置上类型，jboot.rpc.type = myrpc
 * <p>
 * 通过这三步，就可以扩展自己的Jbootrpc实现
 */
public class JbootSpiLoader {


    /**
     * 通过 SPI 去加载相应的扩展子类
     *
     * @param clazz
     * @param spiName
     * @param <T>
     * @return
     */
    public static <T> T load(Class<T> clazz, String spiName) {
        T returnObject = loadByServiceLoader(clazz, spiName);
        if (returnObject != null) return returnObject;

        if (StrUtil.isBlank(spiName)) {
            return null;
        }

        List<Class<T>> classes = ClassScanner.scanSubClass(clazz);
        if (classes == null || classes.isEmpty()) {
            return null;
        }

        for (Class<T> c : classes) {
            JbootSpi spiConfig = c.getAnnotation(JbootSpi.class);
            if (spiConfig != null && spiName.equals(AnnotationUtil.get(spiConfig.value()))) {
                return ClassUtil.newInstance(c);
            }
        }
        return null;
    }

    /**
     * 通过 ServiceLoader 加载
     *
     * @param clazz
     * @param spiName
     * @param <T>
     * @return
     */
    public static <T> T loadByServiceLoader(Class<T> clazz, String spiName) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        Iterator<T> iterator = serviceLoader.iterator();

        while (iterator.hasNext()) {
            T returnObject = iterator.next();

            if (spiName == null) {
                return returnObject;
            }

            JbootSpi spiConfig = returnObject.getClass().getAnnotation(JbootSpi.class);
            if (spiConfig == null) {
                continue;
            }

            if (spiName.equals(AnnotationUtil.get(spiConfig.value()))) {
                return returnObject;
            }
        }

        return null;
    }
}
