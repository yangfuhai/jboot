package io.jboot.core.rpc.motan;

import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.proxy.ProxyFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 扩展 motan 的代理类
 * 用于 Hystrix 的控制和统计
 */
@SpiMeta(name = "jboot")
public class JbootMotanProxyFactory implements ProxyFactory {


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

            return handler.invoke(proxy, method, args);

//            return Jboot.hystrix(new HystrixRunnable() {
//                @Override
//                public Object run() {
//                    try {
//                        return handler.invoke(proxy, method, args);
//                    } catch (Throwable throwable) {
//                        throwable.printStackTrace();
//                    }
//                    return null;
//                }
//            });
        }
    }
}
