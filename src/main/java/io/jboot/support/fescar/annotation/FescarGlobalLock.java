package io.jboot.support.fescar.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * declare the transaction only execute in single local RM,<br/>
 * but the transaction need to ensure records to update(or select for update) is not in global transaction middle
 * stage<br/>
 *
 * use this annotation instead of GlobalTransaction in the situation mentioned above will help performance.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface FescarGlobalLock {
}
