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
package io.jboot.support.seata.interceptor;

import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.Interceptors;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.support.seata.annotation.SeataGlobalLock;
import io.jboot.support.seata.annotation.SeataGlobalTransactional;

import java.lang.reflect.Method;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
@AutoLoad
public class SeataGlobalInterceptorBuilder implements InterceptorBuilder {


    @Override
    public void build(Class<?> serviceClass, Method method, Interceptors interceptors) {

        final SeataGlobalTransactional globalTrxAnno = method.getAnnotation(SeataGlobalTransactional.class);
        final SeataGlobalLock globalLockAnno = method.getAnnotation(SeataGlobalLock.class);

        if (globalTrxAnno != null || globalLockAnno != null){
            interceptors.add(new SeataGlobalTransactionalInterceptor());
        }
    }


}
