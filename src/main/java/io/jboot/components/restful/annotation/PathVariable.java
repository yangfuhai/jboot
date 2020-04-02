package io.jboot.components.restful.annotation;

import java.lang.annotation.*;

/**
 * 路径参数注解
 * /user/{id}/cards
 * 支持如下类型参数注入：
 *  string
 *  int
 *  double
 *  float
 *  boolean
 *  long
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface PathVariable {

    /**
     * 如果为空则默认为参数名本身
     * @return
     */
    String value() default "";

}
