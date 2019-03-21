package io.jboot.components.fescar.config.rpc.dubbo.interceptor;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fescar.core.context.RootContext;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import io.jboot.exception.JbootException;
import io.jboot.web.controller.JbootController;

/**
 * 
 * @author Hobbit
 *
 */
public class JbootTransactionPropagationInterceptor implements Interceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(JbootTransactionPropagationInterceptor.class);

	public void intercept(Invocation inv) {
		String xid = RootContext.getXID();
		String rpcXid = RpcContext.getContext().getAttachment(RootContext.KEY_XID);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("xid in RootContext[" + xid + "] xid in RpcContext[" + rpcXid + "]");
		}
		boolean bind = false;
		if (xid != null) {
			RpcContext.getContext().setAttachment(RootContext.KEY_XID, xid);
		} else {
			if (rpcXid != null) {
				RootContext.bind(rpcXid);
				bind = true;
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("bind[" + rpcXid + "] to RootContext");
				}
			}
		}
		try {
			inv.invoke();

		} catch (JbootException e) {
			if (inv.getTarget() instanceof JbootController) {
				JbootController controller = inv.getTarget();
				LOGGER.debug(controller.getClass().getSimpleName() + " Exception:" + e.getMessage());
				e.printStackTrace();
			}
		}

		finally {
			if (bind) {
				String unbindXid = RootContext.unbind();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("unbind[" + unbindXid + "] from RootContext");
				}
				if (!rpcXid.equalsIgnoreCase(unbindXid)) {
					LOGGER.warn("xid in change during RPC from " + rpcXid + " to " + unbindXid);
					if (unbindXid != null) {
						RootContext.bind(unbindXid);
						LOGGER.warn("bind [" + unbindXid + "] back to RootContext");
					}
				}
			}
		}

	}

}
