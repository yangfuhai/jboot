
package io.jboot.components.fescar.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Global transactional.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface JbootGlobalTransactional {

    /**
     * Global transaction timeoutMills in MILLISECONDS.
     *
     * @return timeoutMills in MILLISECONDS.
     */
    int timeoutMills() default 60000;

    /**
     * Given name of the global transaction instance.
     *
     * @return Given name.
     */
    String name() default "";

}
