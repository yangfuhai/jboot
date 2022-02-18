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
package io.jboot.aop.javassist;

import com.jfinal.kit.LogKit;
import com.jfinal.proxy.ProxyFactory;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;

import java.util.function.Function;

/**
 * JbootCglibProxyFactory 用于扩展 cglib 的代理模式
 *
 * <pre>
 * 配置方法：
 * public void configConstant(Constants me) {
 *     ProxyManager.me().setProxyFactory(new JbootJavassistProxyFactory());
 * }
 * </pre>
 */
public class JbootJavassistProxyFactory extends ProxyFactory {

    /**
     * 方便在单元测试的时候，可以对任意 class 进行 Mock
     */
    private static Function<Class<?>, MethodHandler> methodInterceptor = aClass -> new JbootJavassistHandler();

    public static Function<Class<?>, MethodHandler> getMethodInterceptor() {
        return methodInterceptor;
    }

    public static void setMethodInterceptor(Function<Class<?>, MethodHandler> methodInterceptor) {
        JbootJavassistProxyFactory.methodInterceptor = methodInterceptor;
    }

    @Override
    public <T> T get(Class<T> target) {
        javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();
        factory.setSuperclass(target);
        final Class<?> proxyClass = factory.createClass();


        T proxyObject = null;
        try {
            proxyObject = (T) proxyClass.newInstance();
            ((ProxyObject) proxyObject).setHandler(methodInterceptor.apply(target));
        } catch (Throwable e) {
            LogKit.error(e.toString(), e);
        }

        return proxyObject;
    }


}



