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

import com.jfinal.aop.Invocation;
import io.jboot.support.seata.JbootSeataManager;
import io.jboot.support.seata.annotation.SeataGlobalTransactional;
import io.seata.common.exception.ShouldNeverHappenException;
import io.seata.common.util.StringUtils;
import io.seata.tm.api.FailureHandler;
import io.seata.tm.api.TransactionalExecutor;
import io.seata.tm.api.transaction.NoRollbackRule;
import io.seata.tm.api.transaction.RollbackRule;
import io.seata.tm.api.transaction.TransactionInfo;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SeataGlobalTransactionHandler {

	public static Object handleGlobalTransaction(final Invocation invocation,
			final SeataGlobalTransactional globalTrxAnno) throws Throwable {
		try {
			return JbootSeataManager.me().getTransactionalTemplate().execute(new TransactionalExecutor() {

				@Override
				public Object execute() throws Throwable {
					invocation.invoke();
					return invocation.getReturnValue();
				}

				public String name() {
					String name = globalTrxAnno.name();
					if (!StringUtils.isNullOrEmpty(name)) {
						return name;
					}
					return formatMethod(invocation.getMethod());
				}

				@Override
				public TransactionInfo getTransactionInfo() {
					TransactionInfo transactionInfo = new TransactionInfo();
					transactionInfo.setTimeOut(globalTrxAnno.timeoutMills());
					transactionInfo.setName(name());
					Set<RollbackRule> rollbackRules = new LinkedHashSet<>();
					for (Class<?> rbRule : globalTrxAnno.rollbackFor()) {
						rollbackRules.add(new RollbackRule(rbRule));
					}
					for (String rbRule : globalTrxAnno.rollbackForClassName()) {
						rollbackRules.add(new RollbackRule(rbRule));
					}
					for (Class<?> rbRule : globalTrxAnno.noRollbackFor()) {
						rollbackRules.add(new NoRollbackRule(rbRule));
					}
					for (String rbRule : globalTrxAnno.noRollbackForClassName()) {
						rollbackRules.add(new NoRollbackRule(rbRule));
					}
					transactionInfo.setRollbackRules(rollbackRules);
					return transactionInfo;
				}

			});

		} catch (TransactionalExecutor.ExecutionException e) {
			FailureHandler failureHandler = JbootSeataManager.me().getFailureHandler();
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
		String paramTypes = Arrays.stream(method.getParameterTypes())
				.map(Class::getName)
				.collect(Collectors.joining(", ", "(", ")"));
		return method.getName() + paramTypes;
	}
}
