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
package io.jboot.web.handler;

import com.google.common.collect.Sets;
import com.jfinal.aop.Aop;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.*;
import com.jfinal.log.Log;
import com.jfinal.render.RedirectRender;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.fixedinterceptor.FixedInvocation;
import io.jboot.web.flashmessage.FlashMessageManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web
 */
public class JbootActionHandler extends ActionHandler {


    private static final Log log = Log.getLog(JbootActionHandler.class);

    /**
     * 方便子类复写、从而可以实现 自定义Action的功能
     *
     * @param target
     * @param urlPara
     * @return
     */
    public Action getAction(String target, String[] urlPara) {
        return actionMapping.getAction(target, urlPara);
    }


    /**
     * handle
     * 1: Action action = actionMapping.getAction(target)
     * 2: new Invocation(...).invoke()
     * 3: render(...)
     */
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (target.indexOf('.') != -1) {
            return;
        }

        isHandled[0] = true;
        String[] urlPara = {null};
        Action action = getAction(target, urlPara);

        if (action == null) {
            if (log.isWarnEnabled()) {
                String qs = request.getQueryString();
                log.warn("404 Action Not Found: " + (qs == null ? target : target + "?" + qs));
            }
            renderManager.getRenderFactory().getErrorRender(404).setContext(request, response).render();
            return;
        }

        //对拦截器进行注入
        injectActionInterceptors(action);

        Controller controller = null;
        try {
            // Controller controller = action.getControllerClass().newInstance();
            controller = controllerFactory.getController(action.getControllerClass());
            JbootControllerContext.hold(controller);
//            controller.init(request, response, urlPara[0]);
            CPI._init_(controller, action, request, response, urlPara[0]);

            Invocation invocation = new Invocation(action, controller);
            if (devMode) {
                if (ActionReporter.isReportAfterInvocation(request)) {
                    invokeInvocation(invocation);
                    ActionReporter.report(target, controller, action);
                } else {
                    ActionReporter.report(target, controller, action);
                    invokeInvocation(invocation);
                }
            } else {
                invokeInvocation(invocation);
            }

            Render render = controller.getRender();
            if (render instanceof ForwardActionRender) {
                String actionUrl = ((ForwardActionRender) render).getActionUrl();
                if (target.equals(actionUrl)) {
                    throw new RuntimeException("The forward action url is the same as before.");
                } else {
                    handle(actionUrl, request, response, isHandled);
                }
                return;
            }

            if (render == null) {
                render = renderManager.getRenderFactory().getDefaultRender(action.getViewPath() + action.getMethodName());
            }


            boolean isRedirect = render instanceof RedirectRender;


            /**
             * 如果当前是redirect
             * 尝试设置 FlashMessage 数据到 session
             */
            if (isRedirect) {
                FlashMessageManager.me().init(controller);
            }
            /**
             * 如果当前不是redirect
             * 尝试去渲染 FlashMessage 的数据
             */
            else {
                FlashMessageManager.me().renderTo(controller);
            }

            render.setContext(request, response, action.getViewPath()).render();

            /**
             * 如果当前不是redirect，那么尝试去清空FlashMessage数据
             */
            if (!isRedirect) {
                FlashMessageManager.me().release(controller);
            }

        } catch (RenderException e) {
            if (log.isErrorEnabled()) {
                String qs = request.getQueryString();
                log.error(qs == null ? target : target + "?" + qs, e);
            }
        } catch (ActionException e) {
            int errorCode = e.getErrorCode();
            String msg = null;
            if (errorCode == 404) {
                msg = "404 Not Found: ";
            } else if (errorCode == 401) {
                msg = "401 Unauthorized: ";
            } else if (errorCode == 403) {
                msg = "403 Forbidden: ";
            }

            if (msg != null) {
                if (log.isWarnEnabled()) {
                    String qs = request.getQueryString();
                    log.warn(msg + (qs == null ? target : target + "?" + qs));
                }
            } else {
                if (log.isErrorEnabled()) {
                    String qs = request.getQueryString();
                    log.error(qs == null ? target : target + "?" + qs, e);
                }
            }

            e.getErrorRender().setContext(request, response, action.getViewPath()).render();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                String qs = request.getQueryString();
                log.error(qs == null ? target : target + "?" + qs, e);
            }
            renderManager.getRenderFactory().getErrorRender(500).setContext(request, response, action.getViewPath()).render();
        } finally {
            if (controller != null) {

                JbootControllerContext.release();

//              controller.clear();
                CPI._clear_(controller);
            }
        }
    }


    private void invokeInvocation(Invocation inv) {
        new FixedInvocation(inv).invoke();
    }

    static Set<Action> injectedActions = Sets.newConcurrentHashSet();

    /**
     * 对所有拦截器进行注入
     *
     * @param action
     */
    private void injectActionInterceptors(Action action) {

        //获取这个拦截器下的所有拦截器
        //如果没有拦截器，直接返回
        Interceptor[] interceptors = action.getInterceptors();
        if (interceptors == null || interceptors.length == 0) {
            return;
        }

        //如果注入过了，就没必要再次注入
        //因为拦截器是在整个系统中是单例的
        if (injectedActions.contains(action)) {
            return;
        }

        //对所有拦截器进行注入
        for (Interceptor interceptor : interceptors) {
            Aop.inject(interceptor);
        }

        injectedActions.add(action);
    }
}
