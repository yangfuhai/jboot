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
package io.jboot.core.rpc.motan;

import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.proxy.ProxyFactory;
import io.jboot.Jboot;
import io.jboot.component.hystrix.JbootHystrixCommand;
import io.jboot.component.opentracing.JbootSpanContext;
import io.jboot.core.rpc.JbootrpcConfig;
import io.jboot.core.rpc.JbootrpcManager;
import io.jboot.utils.StringUtils;
import io.opentracing.Span;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 扩展 motan 的代理类
 * 用于 Hystrix 的控制和统计
 */
@SpiMeta(name = "jboot")
public class JbootMotanProxyFactory implements ProxyFactory {


    static JbootrpcConfig rpcConfig = Jboot.config(JbootrpcConfig.class);


    @Override
    public <T> T getProxy(Class<T> clz, InvocationHandler invocationHandler) {
        // 默认是 jdkProxy
        // return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clz}, invocationHandler);
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clz}, new JbootInvocationHandler(invocationHandler));
    }


    /**
     * InvocationHandler 的代理类，InvocationHandler在motan内部创建
     * JbootInvocationHandler代理后，可以对某个方法执行之前做些额外的操作：例如通过 Hystrix 包装
     */
    public static class JbootInvocationHandler implements InvocationHandler {
        private final InvocationHandler handler;

        public JbootInvocationHandler(InvocationHandler handler) {
            this.handler = handler;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if (!rpcConfig.isHystrixEnable()) {
                return handler.invoke(proxy, method, args);
            }

            /**
             * 过滤系统方法，不走hystrix
             */
            if ("hashCode".equals(method.getName())
                    || "toString".equals(method.getName())
                    || "equals".equals(method.getName())
                    || "getClass".equals(method.getName())) {

                return handler.invoke(proxy, method, args);
            }

            final Span span = JbootMotanTracingFilter.getActiveSpan();


            String key = rpcConfig.getHystrixKeyByMethod(method.getName());
            if (StringUtils.isBlank(key) && rpcConfig.isHystrixAutoConfig()) {
                key = method.getDeclaringClass().getName() + "." + method.getName();
            }


            return StringUtils.isBlank(key)
                    ? handler.invoke(proxy, method, args)
                    : Jboot.hystrix(new JbootHystrixCommand(key, rpcConfig.getHystrixTimeout()) {

                @Override
                public Object run() throws Exception {
                    try {
                        JbootSpanContext.add(span);

                        return handler.invoke(proxy, method, args);
                    } catch (Throwable throwable) {
                        throw (Exception) throwable;
                    } finally {
                        JbootSpanContext.release();
                    }

                }

                @Override
                public Object getFallback() {
                    return JbootrpcManager.me().getHystrixFallbackListener().onFallback(proxy, method, args, this, this.getExecutionException());
                }
            });


        }
    }
}
