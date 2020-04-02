package io.jboot.components.restful.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface PostMapping {

    /**
     * url mapping
     * @return
     */
    String value() default "";

}
