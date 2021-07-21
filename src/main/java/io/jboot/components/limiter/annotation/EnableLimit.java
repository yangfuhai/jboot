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
package io.jboot.components.limiter.annotation;

import io.jboot.components.limiter.LimitScope;
import io.jboot.components.limiter.LimitType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface EnableLimit {

    /**
     * 资源名称
     *
     * @return
     */
    String resource() default "";

    /**
     * 类型 ：
     *
     * @return
     */
    String type() default LimitType.TOKEN_BUCKET;


    /**
     * 作用域，默认为单节点本地限次
     */
    LimitScope scope() default LimitScope.NODE;

    /**
     * 频率
     *
     * @return
     */
    int rate();


    /**
     * 降级方法
     *
     * @return
     */
    String fallback() default "";


}
