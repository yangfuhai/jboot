package io.jboot.web.limitation.annotation;

import java.lang.annotation.*;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.limitation.annotation
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EnableUserRateLimit {

    double rate(); //每秒钟允许通过的次数

    /**
     * 被限流后给用户的反馈操作
     * 支持：json，render，text，redirect
     *
     * @return
     */
    String limitAction() default "";

    /**
     * 被限流后给客户端的响应，响应的内容根据 action 的类型来渲染
     *
     * @return
     */
    String limitContent() default "";
    
}
