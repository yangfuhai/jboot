package io.jboot.components.restful.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ResponseHeaders {

    ResponseHeader[] value();

}
