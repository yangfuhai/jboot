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
package io.jboot.support.seata.tcc;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.log.Log;
import io.jboot.support.seata.JbootSeataManager;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.seata.common.util.StringUtils;
import io.seata.core.context.RootContext;
import io.seata.core.model.BranchType;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import io.seata.rm.tcc.remoting.RemotingDesc;

import java.lang.reflect.Method;


/**
 * TCC Interceptor
 * <p>
 * 参考： https://github.com/seata/seata/blob/develop/spring/src/main/java/io/seata/spring/tcc/TccActionInterceptor.java
 *
 * @author zhangsen
 */
public class TccActionInterceptor implements Interceptor, FixedInterceptor {

    private static final Log LOGGER = Log.getLog(TccActionInterceptor.class);

    private ActionInterceptorHandler actionInterceptorHandler = new ActionInterceptorHandler();

    /**
     * remoting bean info
     */
    protected RemotingDesc remotingDesc;

    /**
     * Instantiates a new Tcc action interceptor.
     */
    public TccActionInterceptor() {

    }

    /**
     * Instantiates a new Tcc action interceptor.
     *
     * @param remotingDesc the remoting desc
     */
    public TccActionInterceptor(RemotingDesc remotingDesc) {
        this.remotingDesc = remotingDesc;
    }

    @Override
    public void intercept(Invocation inv) {

        if (!JbootSeataManager.me().isEnable()) {
            inv.invoke();
            return;
        }

        if (!RootContext.inGlobalTransaction()) {
            // not in transaction
            inv.invoke();
            return;
        }
        Method method = inv.getMethod();
        TwoPhaseBusinessAction businessAction = method.getAnnotation(TwoPhaseBusinessAction.class);
        //try method
        if (businessAction != null) {
            //save the xid
            String xid = RootContext.getXID();
            //clear the context
            String previousBranchType = RootContext.getBranchType();
            RootContext.bindBranchType(BranchType.TCC);
            try {
                Object[] methodArgs = inv.getArgs();
                //Handler the TCC Aspect
                actionInterceptorHandler.proceed(method, methodArgs, xid, businessAction, inv);
                return;
            } finally {
                RootContext.unbindBranchType();
                //restore the TCC branchType if exists
                if (StringUtils.equals(BranchType.TCC.name(), previousBranchType)) {
                    RootContext.bindBranchType(BranchType.TCC);
                }
            }
        } else {
            inv.invoke();
        }
        return;
    }

}
