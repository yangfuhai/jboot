package io.jboot.web.controller.annotation;

import java.lang.annotation.*;

/**
 * Created by michael on 16/10/29.
 */
@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface EmptyParaValidate {

    String[] value();

    String errorRedirect() default "";

}
