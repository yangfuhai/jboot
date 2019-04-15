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
import com.jfinal.aop.Invocation;
import io.jboot.support.fescar.JbootFescarManager;
import io.jboot.support.fescar.annotation.FescarGlobalTransactional;

import java.lang.reflect.Method;

public class FescarGlobalTransactionHandler {

    public static Object handleGlobalTransaction(final Invocation invocation, final FescarGlobalTransactional globalTrxAnno) throws Throwable {
        try {
            return JbootFescarManager.me().getTransactionalTemplate()
                    .execute(new TransactionalExecutor() {
                        public Object execute() {
                            invocation.invoke();
                            return invocation.getReturnValue();
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
            FailureHandler failureHandler = JbootFescarManager.me().getFailureHandler();
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

    private static String formatMethod(Method method) {
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
        return sb.toString();
    }
}
