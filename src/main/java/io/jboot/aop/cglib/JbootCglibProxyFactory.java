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
package io.jboot.aop.cglib;

import com.jfinal.proxy.ProxyFactory;
import net.sf.cglib.proxy.MethodInterceptor;

import java.util.function.Function;

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

    /**
     * 方便在单元测试的时候，可以对任意 class 进行 Mock
     */
    private static Function<Class<?>, MethodInterceptor> methodInterceptor = aClass -> new JbootCglibCallback();

    public static Function<Class<?>, MethodInterceptor> getMethodInterceptor() {
        return methodInterceptor;
    }

    public static void setMethodInterceptor(Function<Class<?>, MethodInterceptor> methodInterceptor) {
        JbootCglibProxyFactory.methodInterceptor = methodInterceptor;
    }

    @Override
    public <T> T get(Class<T> target) {
        try {
            return (T) net.sf.cglib.proxy.Enhancer.create(target, methodInterceptor.apply(target));
        } catch (Throwable e) {
            //原始的错误，无法给出哪个类无法被创建，错误信息不够友好
            throw new RuntimeException("Can not create object for class:\"" + target.getName() + "\", cause：" + e.getMessage(), e);
        }
    }


}



