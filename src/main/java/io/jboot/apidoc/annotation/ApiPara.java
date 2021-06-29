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
package io.jboot.apidoc.annotation;

import io.jboot.apidoc.HttpMethod;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface ApiPara {

    /**
     * 标题
     * @return
     */
    String value();

    /**
     * 参数名称
     * @return
     */
    String name() default "";

    /**
     * 描述
     * @return
     */
    String notes() default "";

    /**
     * 数据类型
     * @return
     */
    Class<?> dataType() default void.class;

    /**
     * 要求通过哪些方法传入，比如只能通过 post 传入
     * @return
     */
    HttpMethod[] method() default {};

}