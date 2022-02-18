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
package io.jboot.support.seata.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.support.seata.JbootSeataManager;
import io.jboot.support.seata.annotation.SeataGlobalLock;
import io.jboot.support.seata.annotation.SeataGlobalTransactional;
import io.seata.core.model.GlobalLockConfig;
import io.seata.rm.GlobalLockExecutor;

import java.lang.reflect.Method;

/***
 *
 * @author Hobbit Leon_wy@163.com , Michael Yang (fuhai99@gmail.com)
 * 参考：https://github.com/seata/seata/blob/develop/spring/src/main/java/
 * io/seata/spring/annotation/GlobalTransactionalInterceptor.java
 *
 */
public class SeataGlobalTransactionalInterceptor implements Interceptor {

    public SeataGlobalTransactionalInterceptor() {
    }

    @Override
    public void intercept(Invocation inv) {
        if (!JbootSeataManager.me().isEnable()) {
            inv.invoke();
            return;
        }
        Method method = inv.getMethod();
        final SeataGlobalTransactional globalTrxAnno = method.getAnnotation(SeataGlobalTransactional.class);
        final SeataGlobalLock globalLockAnno = method.getAnnotation(SeataGlobalLock.class);
        try {
            if (globalTrxAnno != null) {
                handleGlobalTransaction(inv, globalTrxAnno);
            } else if (globalLockAnno != null) {
                handleGlobalLock(inv,globalLockAnno);
            } else {
                inv.invoke();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private void handleGlobalLock(final Invocation inv, final SeataGlobalLock globalLockAnno) throws Throwable {
        JbootSeataManager.me().getGlobalLockTemplate().execute(new GlobalLockExecutor() {
            @Override
            public Object execute() throws Throwable {
                inv.invoke();
                return inv.getReturnValue();
            }

            @Override
            public GlobalLockConfig getGlobalLockConfig() {
                GlobalLockConfig config = new GlobalLockConfig();
                config.setLockRetryInternal(globalLockAnno.lockRetryInternal());
                config.setLockRetryTimes(globalLockAnno.lockRetryTimes());
                return config;
            }
        });
    }

    private Object handleGlobalTransaction(final Invocation invocation, final SeataGlobalTransactional globalTrxAnno) throws Throwable {
       return SeataGlobalTransactionHandler.handleGlobalTransaction(invocation,globalTrxAnno);
    }


}
