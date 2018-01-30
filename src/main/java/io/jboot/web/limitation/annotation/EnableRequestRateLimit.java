/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
public @interface EnableRequestRateLimit {

    double rate(); //每秒钟允许通过的次数

    /**
     * 被限流后给用户的反馈操作
     * 支持：json，render，text，redirect
     *
     * @return
     */
    String renderType() default "";

    /**
     * 被限流后给客户端的响应，响应的内容根据 action 的类型来渲染
     *
     * @return
     */
    String renderContent() default "";

}
