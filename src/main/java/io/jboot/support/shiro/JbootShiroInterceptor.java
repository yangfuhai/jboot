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

        JbootShiroManager.me().getInvokeListener().onInvokeBefore(inv);
        AuthorizeResult result = JbootShiroManager.me().invoke(inv.getActionKey());
        JbootShiroManager.me().getInvokeListener().onInvokeAfter(inv, result == null ? AuthorizeResult.ok() : result);
    }

}
