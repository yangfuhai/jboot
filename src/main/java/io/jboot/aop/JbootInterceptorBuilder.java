/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.aop;

import com.jfinal.aop.Interceptor;

import java.lang.reflect.Method;

/**
 * @author michael yang (fuhai999@gmail.com)
 * 
 * JbootInterceptorBuilder 用于自定义每个服务方法的拦截器构建
 *
 * <pre>
 * 配置方法：
 * public void onInit() {
 *     JbootAopFactory.me().addInterceptorBuilder(new MyInterceptorBuilder());
 * }
 * </pre>
 */
public interface JbootInterceptorBuilder {
    Interceptor[] build(Class<?> serviceClass, Method method);
}
