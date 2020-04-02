package io.jboot.components.restful.annotation;

import java.lang.annotation.*;

/**
 * 标注controller action是一个下载响应，并且需要action自行处理response
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DownloadResponse {
}
