package io.jboot.components.restful.annotation;

import java.lang.annotation.*;

/**
 * 自定义响应头
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ResponseHeader {

    String key();

    String value();

}
