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
package io.jboot.components.rpc.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RPCBean {

    /**
     * Service version, default value is empty string
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";

    /**
     * Service path, default value is empty string
     */
    String path() default "";

    /**
     * Whether to export service, default value is true
     */
    boolean export() default true;

    /**
     * Service token, default value is false
     */
    String token() default "";

    /**
     * Whether the service is deprecated, default value is false
     */
    boolean deprecated() default false;


    /**
     * Whether to register the service to register center, default value is true
     */
    boolean register() default true;

    /**
     * Service weight value, default value is 0
     */
    int weight() default 0;

    /**
     * Service doc, default value is ""
     */
    String document() default "";


    /**
     * Service invocation retry times
     *
     */
    int retries() default 2;

    /**
     * Load balance strategy, legal values include: random, roundrobin, leastactive
     *
     */
    String loadbalance() default "random";


    /**
     * Application bean name
     */
    String application() default "";

    /**
     * Module bean name
     */
    String module() default "";

    /**
     * Provider bean name
     */
    String provider() default "";

    /**
     * Protocol bean names
     */
    String[] protocol() default {};

    /**
     * Monitor bean name
     */
    String monitor() default "";

    /**
     * Registry bean name
     */
    String[] registry() default {};

    /**
     * Service tag name
     */
    String tag() default "";


    //当一个Service类实现对个接口的时候，可以通过这个排除不暴露某个实现接口
    Class[] exclude() default Void.class;
}