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
package io.jboot.support.fescar.interceptor;

import com.alibaba.fescar.common.exception.ShouldNeverHappenException;
import com.alibaba.fescar.common.util.StringUtils;
import com.alibaba.fescar.tm.api.FailureHandler;
import com.alibaba.fescar.tm.api.TransactionalExecutor;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.log.Log;
import io.jboot.support.fescar.FescarManager;
import io.jboot.support.fescar.annotation.FescarGlobalLock;
import io.jboot.support.fescar.annotation.FescarGlobalTransactional;
import io.jboot.web.fixedinterceptor.FixedInterceptor;

import java.lang.reflect.Method;

/***
 *
 * @author Hobbit Leon_wy@163.com , Michael Yang (fuhai99@gmail.com)
 *
 */
public class FescarGlobalTransactionalInterceptor implements Interceptor, FixedInterceptor {

    private static final Log LOGGER = Log.getLog(FescarGlobalTransactionalInterceptor.class);

    public void intercept(Invocation inv) {
        if (!FescarManager.me().isEnable()) {
            inv.invoke();
            return;
        }
        Method method = inv.getMethod();
        final FescarGlobalTransactional globalTrxAnno = method.getAnnotation(FescarGlobalTransactional.class);
        final FescarGlobalLock globalLockAnno = method.getAnnotation(FescarGlobalLock.class);
        try {
            if (globalTrxAnno != null) {
                handleGlobalTransaction(inv, globalTrxAnno);
            } else if (globalLockAnno != null) {
                handleGlobalLock(inv);
            } else {
                inv.invoke();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private void handleGlobalLock(final Invocation inv) throws Exception {
        FescarManager.me().getGlobalLockTemplate().execute(() -> {
            try {
                inv.invoke();
                return null;
            } catch (Throwable e) {
                if (e instanceof Exception) {
                    throw (Exception) e;
                } else {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private Object handleGlobalTransaction(final Invocation invocation, final FescarGlobalTransactional globalTrxAnno)
            throws Throwable {
        try {
            return FescarManager.me().getTransactionalTemplate().execute(new TransactionalExecutor() {
                public Object execute() {
                    invocation.invoke();
                    return null;
                }

                public int timeout() {
                    return globalTrxAnno.timeoutMills();
                }

                public String name() {
                    String name = globalTrxAnno.name();
                    if (!StringUtils.isNullOrEmpty(name)) {
                        return name;
                    }
                    return formatMethod(invocation.getMethod());
                }
            });
        } catch (TransactionalExecutor.ExecutionException e) {
            FailureHandler failureHandler = FescarManager.me().getFailureHandler();
            TransactionalExecutor.Code code = e.getCode();
            switch (code) {
                case RollbackDone:
                    throw e.getOriginalException();
                case BeginFailure:
                    failureHandler.onBeginFailure(e.getTransaction(), e.getCause());
                    throw e.getCause();
                case CommitFailure:
                    failureHandler.onCommitFailure(e.getTransaction(), e.getCause());
                    throw e.getCause();
                case RollbackFailure:
                    failureHandler.onRollbackFailure(e.getTransaction(), e.getCause());
                    throw e.getCause();
                default:
                    throw new ShouldNeverHappenException("Unknown TransactionalExecutor.Code: " + code);

            }
        }
    }

    private String formatMethod(Method method) {
        StringBuilder sb = new StringBuilder();

        String methodName = method.getName();
        Class<?>[] params = method.getParameterTypes();
        sb.append(methodName);
        sb.append("(");

        int paramPos = 0;
        for (Class<?> clazz : params) {
            sb.append(clazz.getName());
            if (++paramPos < params.length) {
                sb.append(",");
            }
        }
        sb.append(")");
        LOGGER.debug(sb.toString());
        return sb.toString();
    }
}
