package io.jboot.components.restful.annotation;

import java.lang.annotation.*;

/**
 * rest controller 标识
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RestController {
}
