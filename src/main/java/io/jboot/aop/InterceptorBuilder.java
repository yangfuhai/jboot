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
import java.util.LinkedList;

/**
 * @author michael yang (fuhai999@gmail.com)
 * 
 * InterceptorHandler 用于控制某个方法已经添加好的拦截器，可以对其删除或者添加
 *
 * <pre>
 * 配置方法：
 * public void onInit() {
 *     JbootAopFactory.me().addInterceptorBuilder(new MyInterceptorBuilder());
 * }
 * </pre>
 */
public interface InterceptorBuilder {
    void build(Class<?> serviceClass, Method method, LinkedList<Interceptor> interceptors);
}
