package io.jboot.components.restful.annotation;

import java.lang.annotation.*;

/**
 * 请求头部信息注解
 *  支持如下类型参数注入：
 *  string
 *  int
 *  double
 *  float
 *  boolean
 *  long
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface RequestHeader {

    /**
     * 如果为空则默认为参数名本身
     * @return
     */
    String value() default "";

    boolean required() default false;

}
