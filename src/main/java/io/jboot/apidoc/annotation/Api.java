/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Api {

    /**
     * 文档标题
     *
     * @return
     */
    String value();

    /**
     * 文档描述
     *
     * @return
     */
    String notes() default "";

    /**
     * 生成的文件路径，不配置的情况下，默认为 Controller 的 mapping 转换，例如 mapping 为：/abc/aaa 转为为 abc_aaa。 / 转为为 index。
     *
     * @return
     */
    String filePath() default "";

    /**
     * 其他类的方法也汇总到此 API 文档里来
     *
     * @return
     */
    Class<?>[] collect() default {};

    /**
     * 生成文档的排序
     *
     * @return
     */
    int orderNo() default 0;

}