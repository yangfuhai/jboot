package io.jboot.components.fescar.annotation;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fescar.common.exception.ShouldNeverHappenException;
import com.alibaba.fescar.common.util.StringUtils;
import com.alibaba.fescar.rm.GlobalLockTemplate;
import com.alibaba.fescar.tm.api.DefaultFailureHandlerImpl;
import com.alibaba.fescar.tm.api.FailureHandler;
import com.alibaba.fescar.tm.api.TransactionalExecutor;
import com.alibaba.fescar.tm.api.TransactionalTemplate;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/***
 * 
 * @author Hobbit Leon_wy@163.com
 *
 */
public class JbootGlobalTransactionalInterceptor implements Interceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(JbootGlobalTransactionalInterceptor.class);
	private static final FailureHandler DEFAULT_FAIL_HANDLER = new DefaultFailureHandlerImpl();

	private final TransactionalTemplate transactionalTemplate = new TransactionalTemplate();
	private final GlobalLockTemplate<Object> globalLockTemplate = new GlobalLockTemplate<Object>();
	private final FailureHandler failureHandler;

	public JbootGlobalTransactionalInterceptor(FailureHandler failureHandler) {
		if (null == failureHandler) {
			failureHandler = DEFAULT_FAIL_HANDLER;
		}
		this.failureHandler = failureHandler;
	}

	public void intercept(Invocation inv) {

		Method method = inv.getMethod();

		final JbootGlobalTransactional globalTrxAnno = method.getAnnotation(JbootGlobalTransactional.class);
		final JbootGlobalLock globalLockAnno = method.getAnnotation(JbootGlobalLock.class);
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
		globalLockTemplate.execute(new Callable<Object>() {
			public Object call() throws Exception {
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
			}
		});
	}

	private Object handleGlobalTransaction(final Invocation invocation, final JbootGlobalTransactional globalTrxAnno)
			throws Throwable {
		try {
			return transactionalTemplate.execute(new TransactionalExecutor() {
				public Object execute() throws Throwable {
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
