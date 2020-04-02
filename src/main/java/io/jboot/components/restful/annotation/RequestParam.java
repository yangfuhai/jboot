package io.jboot.components.restful.annotation;

import java.lang.annotation.*;

/**
 * 请求参数注解
 * 支持如下类型参数：
 *  string / string[]
 *  int / int[]
 *  double / double[]
 *  float / float[]
 *  boolean / boolean[]
 *  long / long[]
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface RequestParam {

    /**
     * 如果为空则默认为参数名本身
     * @return
     */
    String value() default "";

    boolean required() default false;

}
