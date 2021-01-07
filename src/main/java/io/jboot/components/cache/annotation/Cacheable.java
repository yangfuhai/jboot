/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.components.cache.annotation;

import java.lang.annotation.*;

/**
 * @author michael yang
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Cacheable {

    /**
     * 缓存名称
     *
     * @return
     */
    String name();

    /**
     * 缓存的key
     *
     * @return
     */
    String key() default "";

    /**
     * 缓存时间，单位秒
     * 默认情况下，缓存永久有效
     *
     * @return
     */
    int liveSeconds() default 0; // 0，系统配置默认，默认情况下永久有效

    /**
     * 是否对 null 值进行缓存
     *
     * @return
     */
    boolean nullCacheEnable() default false;

    /**
     * 是否对返回的数据进行 Copy 后返回
     *
     * @return
     */
    boolean returnCopyEnable() default false;

    /**
     * 在什么情况下不进行缓存
     * 这里编写的是 JFinal 模板引擎的表达式
     *
     * @return
     */
    String unless() default "";
}