/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Invocation;
import com.jfinal.log.Log;
import io.seata.common.Constants;
import io.seata.common.exception.FrameworkException;
import io.seata.common.util.NetUtil;
import io.seata.common.util.ReflectionUtil;
import io.seata.core.model.BranchType;
import io.seata.rm.DefaultResourceManager;
import io.seata.rm.tcc.TCCResource;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler the TCC Participant Aspect : Setting Context, Creating Branch Record
 * 参考：https://github.com/seata/seata/blob/master/tcc/src/main/java/io/seata/rm/tcc/interceptor/ActionInterceptorHandler.java
 *
 * @author zhangsen/菜农 commit: https://gitee.com/fuhai/jboot/commit/55564bfd9e6eebfc39263291d89592cd16f77498
 */
public class ActionInterceptorHandler {

    private static final Log LOGGER = Log.getLog(TccActionInterceptor.class);

    /**
     * Handler the TCC Aspect
     *
     * @param method         the method
     * @param arguments      the arguments
     * @param businessAction the business action
     * @return map map
     * @throws Throwable the throwable
     */
    public void proceed(Method method, Object[] arguments, String xid, TwoPhaseBusinessAction businessAction,
                                       Invocation invocation)  {

        //TCC name
        String actionName = businessAction.name();
        BusinessActionContext actionContext = new BusinessActionContext();
        actionContext.setXid(xid);
        //set action name
        actionContext.setActionName(actionName);
        Class<?>[] types = method.getParameterTypes();
        Parameter[] parameters = invocation.getMethod().getParameters();
        int argIndex = 0;
        for (Class<?> cls : types) {
            if (cls.getName().equals(BusinessActionContext.class.getName())) {
                arguments[argIndex] = actionContext;
                break;
            }
            argIndex++;
        }
        //Creating Branch Record
        String branchId = doTccActionLogStore(method,parameters, arguments, businessAction, actionContext);
        try {
            registryResource(method, invocation.getTarget(),types, businessAction);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        actionContext.setBranchId(branchId);

        invocation.invoke();
    }

    /**
     * Creating Branch Record
     *
     * @param method         the method
     * @param arguments      the arguments
     * @param businessAction the business action
     * @param actionContext  the action context
     * @return the string
     */
    protected String doTccActionLogStore(Method method, Parameter[] parameters , Object[] arguments, TwoPhaseBusinessAction businessAction,
                                         BusinessActionContext actionContext) {
        String actionName = actionContext.getActionName();
        String xid = actionContext.getXid();
        //
        Map<String, Object> context = fetchActionRequestContext(method, arguments,parameters);
        context.put(Constants.ACTION_START_TIME, System.currentTimeMillis());

        //init business context
        initBusinessContext(context, method, businessAction);
        //Init running environment context
        initFrameworkContext(context);
        actionContext.setActionContext(context);

        //init applicationData
        Map<String, Object> applicationContext = new HashMap<>(4);
        applicationContext.put(Constants.TCC_ACTION_CONTEXT, context);
        String applicationContextStr = JSON.toJSONString(applicationContext);
        try {
            //registry branch record
            Long branchId = DefaultResourceManager.get().branchRegister(BranchType.TCC, actionName, null, xid,
                    applicationContextStr, null);
            return String.valueOf(branchId);
        } catch (Throwable t) {
            String msg = String.format("TCC branch Register error, xid: %s", xid);
            LOGGER.error(msg, t);
            throw new FrameworkException(t, msg);
        }
    }

    /**
     * Init running environment context
     *
     * @param context the context
     */
    protected void initFrameworkContext(Map<String, Object> context) {
        try {
            context.put(Constants.HOST_NAME, NetUtil.getLocalIp());
        } catch (Throwable t) {
            LOGGER.warn("getLocalIP error", t);
        }
    }

    /**
     * Init business context
     *
     * @param context        the context
     * @param method         the method
     * @param businessAction the business action
     */
    protected void initBusinessContext(Map<String, Object> context, Method method,
                                       TwoPhaseBusinessAction businessAction) {
        if (method != null) {
            //the phase one method name
            context.put(Constants.PREPARE_METHOD, method.getName());
        }
        if (businessAction != null) {
            //the phase two method name
            context.put(Constants.COMMIT_METHOD, businessAction.commitMethod());
            context.put(Constants.ROLLBACK_METHOD, businessAction.rollbackMethod());
            context.put(Constants.ACTION_NAME, businessAction.name());
        }
    }

    /**
     * Extracting context data from parameters, add them to the context
     *
     * @param method    the method
     * @param arguments the arguments
     * @return map map
     */
    protected Map<String, Object> fetchActionRequestContext(Method method, Object[] arguments,Parameter[] parameters) {
        Map<String, Object> context = new HashMap<>(8);
        int x = 0;
        for (Parameter p : parameters) {
            if (!p.isNamePresent()) {
                // 必须通过添加 -parameters 进行编译，才可以获取 Parameter 的编译前的名字
                throw new RuntimeException(" Maven or IDE config is error. see http://www.jfinal.com/doc/3-3 ");
            }
            if (!"io.seata.rm.tcc.api.BusinessActionContext".equals(p.getType().getName())) {
                context.put(p.getName(), arguments[x]);
            }
            x++;
        }
        return context;
    }

    public void registryResource(Method m,  Object interfaceClass,  Class[] arguments,  TwoPhaseBusinessAction businessAction) throws NoSuchMethodException {
        if (businessAction != null) {
            TCCResource tccResource = new TCCResource();
            tccResource.setActionName(businessAction.name());
            tccResource.setTargetBean(interfaceClass);
            tccResource.setPrepareMethod(m);
            tccResource.setCommitMethodName(businessAction.commitMethod());
            tccResource.setCommitMethod(ReflectionUtil
                    .getMethod(interfaceClass.getClass(), businessAction.commitMethod(),
                            new Class[] {BusinessActionContext.class}));
            tccResource.setRollbackMethodName(businessAction.rollbackMethod());
            tccResource.setRollbackMethod(ReflectionUtil
                    .getMethod(interfaceClass.getClass(), businessAction.rollbackMethod(),
                            new Class[] {BusinessActionContext.class}));
            //registry tcc resource
            DefaultResourceManager.get().registerResource(tccResource);
        }
    }


}
