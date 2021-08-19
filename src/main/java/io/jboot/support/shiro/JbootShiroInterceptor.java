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
package io.jboot.support.shiro;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.support.shiro.processer.AuthorizeResult;

/**
 * Shiro 拦截器
 */
public class JbootShiroInterceptor implements Interceptor {


    @Override
    public void intercept(Invocation inv) {
        // 优先执行 onInvokeBefore，得到 AuthorizeResult
        // 如果 AuthorizeResult 不为 null，则说用户自定义了其认证方式，比如 jwt、oss 等，此时直接返回给 onInvokeAfter
        // 如果 AuthorizeResult 为 null，则有系统去执行（主要是去判断 Shiro 注解，然后通过对于的 Processer 去执行 ）

        JbootShiroManager manager = JbootShiroManager.me();

        AuthorizeResult result = manager.getInvokeListener().onInvokeBefore(inv);

        if (result == null) {
            result = manager.invoke(inv.getActionKey());
        }

        if (result == null) {
            result = AuthorizeResult.ok();
        }

        manager.getInvokeListener().onInvokeAfter(inv, result);
    }

}
