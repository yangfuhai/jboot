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
package io.jboot.components.limiter;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jboot.Jboot;
import io.jboot.exception.JbootException;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Method;

/**
 * 默认的限流处理器
 */
public class LimitFallbackProcesserDefault implements LimitFallbackProcesser {

    protected LimitConfig config = Jboot.config(LimitConfig.class);

    public void process(String resource, String fallback, Invocation inv) {

        if (StrUtil.isNotBlank(fallback)) {
            doProcessFallback(fallback, inv);
            return;
        }

        if (inv.isActionInvocation()) {
            doProcessWebLimit(resource, inv);
        } else {
            doProcessServiceLimit(resource, inv);
        }
    }

    protected void doProcessFallback(String fallback, Invocation inv) {
        Method method = getMethodByName(fallback, inv);
        if (method == null) {
            throw new JbootException("can not find method[" + fallback + "] in class " +
                    ClassUtil.getUsefulClass(inv.getTarget().getClass()));
        }

        try {
            method.setAccessible(true);
            Object invokeValue = method.getParameterCount() == 0
                    ? method.invoke(inv.getTarget())
                    : method.invoke(inv.getTarget(), inv.getArgs());
            inv.setReturnValue(invokeValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected Method getMethodByName(String methodName, Invocation inv) {
        Class clazz = ClassUtil.getUsefulClass(inv.getTarget().getClass());
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            if (methodName.equals(m.getName())) {
                return m;
            }
        }
        return null;
    }


    /**
     * 处理 Controller 的限流
     *
     * @param resource
     * @param inv
     */
    protected void doProcessWebLimit(String resource, Invocation inv) {

        Controller controller = inv.getController();
        controller.getResponse().setStatus(config.getDefaultHttpCode());

        if (RequestUtil.isAjaxRequest(controller.getRequest())) {
            controller.renderJson(config.getDefaultAjaxContent());
        }
        //非ajax的正常请求
        else {
            String limitView = config.getDefaultHtmlView();
            if (limitView != null) {
                controller.render(limitView);
            } else {
                controller.renderText("reqeust limit.");
            }
        }
    }

    /**
     * 处理 Service 层的限流
     *
     * @param resource
     * @param inv
     */
    protected void doProcessServiceLimit(String resource, Invocation inv) {
        if (Jboot.isDevMode()) {
            System.err.println(resource + " is limited , return null");
        }
    }
}
