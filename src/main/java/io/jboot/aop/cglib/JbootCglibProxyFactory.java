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
package io.jboot.aop.cglib;

import com.jfinal.aop.Interceptor;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.SyncWriteMap;
import com.jfinal.proxy.ProxyFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * JbootCglibProxyFactory 用于扩展 cglib 的代理模式
 *
 * <pre>
 * 配置方法：
 * public void configConstant(Constants me) {
 *     ProxyManager.me().setProxyFactory(new JbootCglibProxyFactory());
 * }
 * </pre>
 */
public class JbootCglibProxyFactory extends ProxyFactory {

    @Override
    public <T> T get(Class<T> target) {
        return (T) net.sf.cglib.proxy.Enhancer.create(target, new JbootCglibCallback());
    }

    static class IntersCache {
        private static final Map<MethodKey, Interceptor[]> cache = new SyncWriteMap<>(2048, 0.25F);

        public static void put(MethodKey methodKey, Interceptor[] inters) {
            Objects.requireNonNull(methodKey, "methodKey can not be null");
            Objects.requireNonNull(inters, "inters can not be null");

            cache.putIfAbsent(methodKey, inters);
        }

        public static Interceptor[] get(MethodKey methodKey) {
            return cache.get(methodKey);
        }

        public static MethodKey getMethodKey(Class<?> target, Method method) {
            long paraHash = HashKit.FNV_OFFSET_BASIS_64;
            Class<?>[] paraTypes = method.getParameterTypes();
            for (Class<?> pt : paraTypes) {
                paraHash ^= pt.getName().hashCode();
                paraHash *= HashKit.FNV_PRIME_64;
            }

            return new MethodKey(target.getName().hashCode(), method.getName().hashCode(), paraHash);
        }

        public static void clear() {
            cache.clear();
        }
    }

    static class MethodKey {
        final int classHash;
        final int methodHash;
        final long paraHash;

        MethodKey(int classHash, int methodHash, long paraHash) {
            this.classHash = classHash;
            this.methodHash = methodHash;
            this.paraHash = paraHash;
        }

        @Override
        public int hashCode() {
            return classHash ^ methodHash ^ ((int) paraHash);
        }

        /**
         * 通过比较三部分 hash 值，避免超大规模场景下可能的 key 值碰撞
         * <p>
         * 不必判断 if (methodKey instanceof MethodKey)，因为所有 key 类型必须要相同
         * 不必判断 if (this == methodKey)，因为每次用于取值的 methodKey 都是新建的
         */
        @Override
        public boolean equals(Object methodKey) {
            MethodKey mk = (MethodKey) methodKey;
            return classHash == mk.classHash && methodHash == mk.methodHash && paraHash == mk.paraHash;
        }

        @Override
        public String toString() {
            return "classHash = " + classHash + "\nmethodHash = " + methodHash + "\nparaHash = " + paraHash;
        }
    }
}



