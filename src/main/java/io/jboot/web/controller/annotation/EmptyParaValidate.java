package io.jboot.web.controller.annotation;

import java.lang.annotation.*;


@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface EmptyParaValidate {

    String[] value();

    String errorRedirect() default "";

}
