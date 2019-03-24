/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.fixedinterceptor;

import com.jfinal.aop.Invocation;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @title 不会被 @Clear 清除掉的 拦截器
 * @version V1.0
 * @Package io.jboot.web.handler
 */
public interface FixedInterceptor {
    void intercept(Invocation inv);
}
