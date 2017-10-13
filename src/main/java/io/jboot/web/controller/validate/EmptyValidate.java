package io.jboot.web.controller.validate;

import java.lang.annotation.*;

/**
 * 非空验证注解
 */
@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface EmptyValidate {

    String[] value();

    String errorRedirect() default "";

}
