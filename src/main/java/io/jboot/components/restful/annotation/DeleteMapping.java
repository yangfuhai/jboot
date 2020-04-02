package io.jboot.components.restful.annotation;

import java.lang.annotation.*;

/**
 * 删除delete 方法注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DeleteMapping {

    /**
     * url mapping
     * @return
     */
    String value() default "";

}
