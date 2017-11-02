package io.jboot.web.handler;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.handler
 */
public interface HandlerInterceptor {
    void intercept(HandlerInvocation inv);
}
