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
package io.jboot.core.rpc.dubbo;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyFactory;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyInvoker;
import com.alibaba.dubbo.rpc.proxy.InvokerInvocationHandler;
import io.jboot.Jboot;
import io.jboot.component.hystrix.HystrixRunnable;
import io.jboot.component.hystrix.JbootHystrixConfig;
import io.jboot.utils.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 扩展 dubbo 的代理类
 * 用于 Hystrix 的控制和统计
 */
public class JbootDubboProxyFactory extends AbstractProxyFactory {


    static JbootHystrixConfig hystrixConfig = Jboot.config(JbootHystrixConfig.class);


    @Override
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, new JbootInvocationHandler(invoker));
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException {
        return new AbstractProxyInvoker<T>(proxy, type, url) {
            @Override
            protected Object doInvoke(T proxy, String methodName,
                                      Class<?>[] parameterTypes,
                                      Object[] arguments) throws Throwable {
                Method method = proxy.getClass().getMethod(methodName, parameterTypes);
                return method.invoke(proxy, arguments);
            }
        };
    }


    /**
     * InvocationHandler 的代理类，InvocationHandler在motan内部创建
     * JbootInvocationHandler代理后，可以对某个方法执行之前做些额外的操作：例如通过 Hystrix 包装
     */
    public static class JbootInvocationHandler extends InvokerInvocationHandler {

        public JbootInvocationHandler(Invoker<?> handler) {
            super(handler);
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            String key = hystrixConfig.getKeyByMethod(method.getName());

            return StringUtils.isBlank(key) ?
                    super.invoke(proxy, method, args) : Jboot.hystrix(key, new HystrixRunnable() {
                @Override
                public Object run() {
                    try {
                        return JbootInvocationHandler.super.invoke(proxy, method, args);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    return null;
                }
            });
        }
    }
}
