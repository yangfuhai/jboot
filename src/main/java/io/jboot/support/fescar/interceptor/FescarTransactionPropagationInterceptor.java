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

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fescar.core.context.RootContext;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.log.Log;
import io.jboot.support.fescar.FescarManager;
import io.jboot.web.controller.JbootController;
import io.jboot.web.fixedinterceptor.FixedInterceptor;

/**
 * @author Hobbit, Michael
 */
public class FescarTransactionPropagationInterceptor implements Interceptor, FixedInterceptor {

    private static final Log LOGGER = Log.getLog(FescarTransactionPropagationInterceptor.class);

    public void intercept(Invocation inv) {
        if (!FescarManager.me().isEnable()) {
            inv.invoke();
            return;
        }

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
        } catch (Exception e) {
            if (inv.getTarget() instanceof JbootController) {
                JbootController controller = inv.getTarget();
                LOGGER.error(controller.getClass().getSimpleName() + " Exception:" + e.getMessage());
                throw e;
            }
        } finally {
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
