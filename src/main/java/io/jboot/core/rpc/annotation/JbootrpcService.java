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
package io.jboot.core.rpc.annotation;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.*;

@Inherited
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface JbootrpcService {

    int port() default 0;

    int timeout() default -1;

    int retries() default -1;

    int actives() default -1;

    String group() default "";

    String version() default "";

    String loadbalance() default "";

    String async() default "";

    String check() default "";

    //当一个Service类实现对个接口的时候，可以通过这个排除不暴露某个实现接口
    Class[] exclude() default Void.class;
}