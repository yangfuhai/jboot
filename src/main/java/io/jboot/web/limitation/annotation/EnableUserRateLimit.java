package io.jboot.web.limitation.annotation;

import java.lang.annotation.*;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.limitation.annotation
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EnableUserRateLimit {
}
