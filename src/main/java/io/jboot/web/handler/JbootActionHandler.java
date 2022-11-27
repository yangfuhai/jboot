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
package io.jboot.web.handler;

import com.jfinal.aop.Invocation;
import com.jfinal.core.*;
import com.jfinal.log.Log;
import com.jfinal.render.IRenderFactory;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.jfinal.template.TemplateException;
import io.jboot.app.JbootApplicationConfig;
import io.jboot.components.valid.ValidErrorRender;
import io.jboot.components.valid.ValidException;
import io.jboot.components.valid.ValidUtil;
import io.jboot.utils.ClassUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.render.JbootErrorRender;
import io.jboot.web.render.JbootRenderFactory;
import io.jboot.web.render.JbootReturnValueRender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class JbootActionHandler extends ActionHandler {


    private static final Log LOG = Log.getLog(JbootActionHandler.class);
    private static final JbootApplicationConfig appConfig = JbootApplicationConfig.get();

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
        return JbootActionReporter.isReportEnable() ? new JbootActionReporterInvocation(action, controller) : new JbootActionInvocation(action, controller);
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
            if (!appConfig.isHandle404()) {
                isHandled[0] = false;
                return;
            }

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

            if (JbootActionReporter.isReportEnable()) {
                long time = System.currentTimeMillis();
                try {
                    doStartRender(target, action, controller, invocation, isHandled);
                } finally {
                    JbootActionReporter.report(target, controller, action, invocation, time);
                }
            } else {
                doStartRender(target, action, controller, invocation, isHandled);
            }

            doAfterRender(action, controller);

        } catch (RenderException e) {
            if (LOG.isErrorEnabled()) {
                String qs = request.getQueryString();
                LOG.error(qs == null ? target : target + "?" + qs, e);
            }
        } catch (ActionException e) {
            if (e.getErrorCode() == 404 && !appConfig.isHandle404()) {
                isHandled[0] = false;
                return;
            }
            handleActionException(target, request, response, action, e);
        } catch (ValidException e) {
            handleValidException(target, request, response, action, e);
        } catch (TemplateException e) {
            handleTemplateException(target, request, response, action, e);
        } catch (Exception e) {
            handleException(target, request, response, action, e);
        } finally {
            JbootControllerContext.release();
            controllerFactory.recycle(controller);
        }
    }

    protected boolean isJspTarget(String target) {
        return target.toLowerCase().contains(".jsp");
    }


    protected void doAfterRender(Action action, Controller controller) {
    }


    protected void doStartRender(String target
            , Action action
            , Controller controller
            , Invocation invocation
            , boolean[] isHandled) {

        invocation.invoke();

        Render render = controller.getRender();
        if (render instanceof ForwardActionRender) {
            String actionUrl = ((ForwardActionRender) render).getActionUrl();
            if (target.equals(actionUrl)) {
                throw new RuntimeException("The forward action url is the same as before.");
            } else {
                handle(actionUrl, controller.getRequest(), controller.getResponse(), isHandled);
            }
        } else {
            if (render == null && void.class != action.getMethod().getReturnType()
                    && renderManager.getRenderFactory() instanceof JbootRenderFactory) {

                JbootRenderFactory factory = (JbootRenderFactory) renderManager.getRenderFactory();
                JbootReturnValueRender returnValueRender = factory.getReturnValueRender(invocation.getReturnValue());

                String forwardTo = returnValueRender.getForwardTo();
                if (forwardTo != null) {
                    handle(getRealForwrdTo(forwardTo, target, action), controller.getRequest(), controller.getResponse(), isHandled);
                    return;
                } else {
                    render = returnValueRender;
                    //重新设置到 Controller，JbootActionReporter 才能 Controller 获取 render 判断 render 类型
                    controller.render(render);
                }
            }

            if (render == null) {
                render = renderManager.getRenderFactory().getDefaultRender(action.getViewPath() + action.getMethodName());

                //重新设置到 Controller，JbootActionReporter 才能 Controller 获取 render 判断 render 类型
                controller.render(render);
            }

            render.setContext(controller.getRequest(), controller.getResponse(), action.getViewPath()).render();
        }
    }

    public String getRealForwrdTo(String forwardTo, String currentTarget, Action action) {
        if ("".equals(forwardTo)) {
            throw new IllegalArgumentException(ClassUtil.buildMethodString(action.getMethod()) + ": The forward key can not be blank.");
        }

        if (forwardTo.startsWith("/")) {
            return forwardTo;
        }


        if (forwardTo.startsWith("./")) {
            return currentTarget.substring(0, currentTarget.lastIndexOf("/")) + forwardTo.substring(1);
        }

        return "/" + forwardTo;
    }


    /**
     * 处理错误信息
     *
     * @param target
     * @param request
     * @param response
     * @param action
     * @param e
     */
    protected void handleActionException(String target, HttpServletRequest request, HttpServletResponse response, Action action, ActionException e) {
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
                    LOG.info(msg, e);
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


    /**
     * 处理参数验证错误
     */
    protected void handleValidException(String target, HttpServletRequest request, HttpServletResponse response, Action action, ValidException validException) {
        if (LOG.isErrorEnabled()) {
//            String qs = request.getQueryString();
//            String targetInfo = qs == null ? target : target + "?" + qs;
//            LOG.error(validException.getReason() + " : " + targetInfo, validException);
            LOG.error("Invalid parameter: " + validException.getReason());
        }
        IRenderFactory factory = renderManager.getRenderFactory();
        if (factory instanceof JbootRenderFactory) {
            ValidErrorRender render = ((JbootRenderFactory) factory).getValidErrorRender(validException);
            render.setContext(request, response, action.getViewPath()).render();
        } else {
            Render render = renderManager.getRenderFactory().getErrorRender(ValidUtil.getErrorCode());
            if (render instanceof JbootErrorRender) {
                ((JbootErrorRender) render).setThrowable(validException);
            }
            render.setContext(request, response, action.getViewPath()).render();
        }
    }


    /**
     * 处理模板错误
     */
    protected void handleTemplateException(String target, HttpServletRequest request, HttpServletResponse response, Action action, TemplateException e) {
        String qs = request.getQueryString();
        String targetInfo = qs == null ? target : target + "?" + qs;
        String info = ClassUtil.buildMethodString(action.getMethod());
        LOG.error(info + " \nQuery: " + targetInfo + "\n", e);

        IRenderFactory factory = renderManager.getRenderFactory();
        if (factory instanceof JbootRenderFactory) {
            ((JbootRenderFactory) factory).getTemplateErrorRender(e).setContext(request, response, action.getViewPath()).render();
        }
    }


    /**
     * 处理其他业务错误
     */
    protected void handleException(String target, HttpServletRequest request, HttpServletResponse response, Action action, Exception e) {
        String qs = request.getQueryString();
        String targetInfo = qs == null ? target : target + "?" + qs;
        String info = ClassUtil.buildMethodString(action.getMethod());
        LOG.error(info + " \nQuery: " + targetInfo + "\n", e);
        renderManager.getRenderFactory().getErrorRender(500).setContext(request, response, action.getViewPath()).render();
    }


}
