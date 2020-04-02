package io.jboot.components.restful.annotation;

import java.lang.annotation.*;

/**
 * Put 请求方法定义
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface PutMapping {

    /**
     * url mapping
     * @return
     */
    String value() default "";

}
