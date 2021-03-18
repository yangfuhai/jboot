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
package io.jboot.web.handler;

import com.jfinal.aop.Invocation;
import com.jfinal.core.*;
import com.jfinal.log.Log;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import io.jboot.components.valid.ValidUtil;
import io.jboot.utils.ClassUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.render.JbootErrorRender;
import io.jboot.web.render.JbootRenderFactory;
import io.jboot.components.valid.ValidException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class JbootActionHandler extends ActionHandler {


    private static final Log LOG = Log.getLog(JbootActionHandler.class);

    /**
     * 方便子类复写、从而可以实现 自定义 Action 的功能
     *
     * @param target
     * @param urlPara
     * @param request
     * @return
     */
    public Action getAction(String target, String[] urlPara, HttpServletRequest request) {
        return this.getAction(target, urlPara);
    }


    /**
     * 方便子类复写、从而可以实现 自定义 Action 的功能
     *
     * @param target
     * @param urlPara
     * @return
     */
    @Override
    protected Action getAction(String target, String[] urlPara) {
        return super.getAction(target, urlPara);
    }

    /**
     * 方便子类复写、从而可以实现 自定义 Invocation 的功能
     *
     * @param action
     * @param controller
     * @return
     */
    public Invocation getInvocation(Action action, Controller controller) {
        return new JbootActionInvocation(action, controller);
    }


    /**
     * handle
     * 1: Action action = actionMapping.getAction(target)
     * 2: new Invocation(...).invoke()
     * 3: render(...)
     */
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (target.lastIndexOf('.') != -1) {
            return;
        }

        isHandled[0] = true;
        String[] urlPara = {null};
        Action action = getAction(target, urlPara, request);

        if (action == null) {
            if (LOG.isWarnEnabled()) {
                String qs = request.getQueryString();
                LOG.warn("404 Action Not Found: " + (qs == null ? target : target + "?" + qs));
            }
            renderManager.getRenderFactory().getErrorRender(404).setContext(request, response).render();
            return;
        }


        Controller controller = null;
        try {
            controller = controllerFactory.getController(action.getControllerClass());
            //controller.init(request, response, urlPara[0]);
            CPI._init_(controller, action, request, response, urlPara[0]);

            JbootControllerContext.hold(controller);

            //Invocation invocation = new Invocation(action, controller);
            Invocation invocation = getInvocation(action, controller);

            if (devMode) {
                long time = System.currentTimeMillis();
                try {
                    doStartRender(target, request, response, isHandled, action, controller, invocation);
                } finally {
                    JbootActionReporter.report(target, controller, action, invocation, time);
                }
            } else {
                doStartRender(target, request, response, isHandled, action, controller, invocation);
            }

        } catch (RenderException e) {
            if (LOG.isErrorEnabled()) {
                String qs = request.getQueryString();
                LOG.error(qs == null ? target : target + "?" + qs, e);
            }
        } catch (ActionException e) {
            handleActionException(target, request, response, action, e);
        } catch (ValidException e) {
            if (LOG.isErrorEnabled()) {
                String qs = request.getQueryString();
                String targetInfo = qs == null ? target : target + "?" + qs;
                LOG.error(e.getReason() + " : " + targetInfo, e);
            }
            Render render = renderManager.getRenderFactory().getErrorRender(ValidUtil.getErrorCode());
            if (render instanceof JbootErrorRender) {
                ((JbootErrorRender) render).setThrowable(e);
            }
            render.setContext(request, response, action.getViewPath()).render();
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                String qs = request.getQueryString();
                String targetInfo = qs == null ? target : target + "?" + qs;
                String info = ClassUtil.buildMethodString(action.getMethod());
                LOG.error(info + " : " + targetInfo, e);
            }
            renderManager.getRenderFactory().getErrorRender(500).setContext(request, response, action.getViewPath()).render();
        } finally {
            JbootControllerContext.release();
            controllerFactory.recycle(controller);
        }
    }

    private void doStartRender(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled, Action action, Controller controller, Invocation invocation) {

        invocation.invoke();

        Render render = controller.getRender();
        if (render instanceof ForwardActionRender) {
            String actionUrl = ((ForwardActionRender) render).getActionUrl();
            if (target.equals(actionUrl)) {
                throw new RuntimeException("The forward action url is the same as before.");
            } else {
                handle(actionUrl, request, response, isHandled);
            }
        } else {
            if (render == null && void.class != action.getMethod().getReturnType() && renderManager.getRenderFactory() instanceof JbootRenderFactory) {
                JbootRenderFactory jbootRenderFactory = (JbootRenderFactory) renderManager.getRenderFactory();
                render = jbootRenderFactory.getReturnValueRender(action, invocation.getReturnValue());
            }

            if (render == null) {
                render = renderManager.getRenderFactory().getDefaultRender(action.getViewPath() + action.getMethodName());
            }

            render.setContext(request, response, action.getViewPath()).render();
        }
    }


    private void handleActionException(String target, HttpServletRequest request, HttpServletResponse response, Action action, ActionException e) {
        int errorCode = e.getErrorCode();
        String msg = null;
        if (errorCode == 404) {
            msg = "404 Not Found: ";
        } else if (errorCode == 400) {
            msg = "400 Bad Request: ";
        } else if (errorCode == 401) {
            msg = "401 Unauthorized: ";
        } else if (errorCode == 403) {
            msg = "403 Forbidden: ";
        }


        if (msg != null) {
            if (errorCode == 404 || errorCode == 401 || errorCode == 403) {
                if (LOG.isWarnEnabled()) {
                    String qs = request.getQueryString();
                    msg = msg + (qs == null ? target : target + "?" + qs);
                    LOG.warn(msg, e);
                }
            } else {
                if (LOG.isErrorEnabled()) {
                    String qs = request.getQueryString();
                    msg = msg + (qs == null ? target : target + "?" + qs);
                    LOG.error(msg, e);
                }
            }
        } else {
            if (LOG.isErrorEnabled()) {
                String qs = request.getQueryString();
                LOG.error(errorCode + " Error: " + (qs == null ? target : target + "?" + qs), e);
            }
        }

        e.getErrorRender().setContext(request, response, action.getViewPath()).render();
    }


}
