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

import com.google.common.base.Splitter;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Action;
import com.jfinal.core.ActionException;
import com.jfinal.core.CPI;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.render.RenderException;
import io.jboot.components.valid.ValidException;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.session.JbootServletRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author 没牙的小朋友 （mjl@nxu.edu.cn）
 * @version V1.0
 */
public class PathVariableActionHandler extends JbootActionHandler {

    private static final Log LOG = Log.getLog(PathVariableActionHandler.class);

    /**
     * handle
     * 1: Action action = actionMapping.getAction(target)
     * 2: new Invocation(...).invoke()
     * 3: render(...)
     */
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (target.lastIndexOf('.') != -1) {
            if (isJspTarget(target)){
                isHandled[0] = true;
                renderManager.getRenderFactory().getErrorRender(404).setContext(request, response).render();
            }
            return;
        }

        isHandled[0] = true;
        //urlPara数组增加第2个元素存储路径参数
        String[] urlPara = {null, null};
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
            //存在封装的路径参数
            if (urlPara[1] != null) {
                Map<String, String> params = Splitter.on("&").withKeyValueSeparator("=").split(urlPara[1]);
                PathVariableWrappedRequest wrappedRequest = new PathVariableWrappedRequest(request, response, params);
                CPI._init_(controller, action, wrappedRequest, response, urlPara[0]);
            } else {
                CPI._init_(controller, action, request, response, urlPara[0]);
            }
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
            handleActionException(target, request, response, action, e);
        } catch (ValidException e) {
            handleValidException(target, request, response, action, e);
        } catch (Exception e) {
            handleException(target, request, response, action, e);
        } finally {
            JbootControllerContext.release();
            controllerFactory.recycle(controller);
        }
    }


//    private void doStartRender(String target
//            , HttpServletRequest request
//            , HttpServletResponse response
//            , boolean[] isHandled
//            , Action action
//            , Controller controller
//            , Invocation invocation) {
//
//        invocation.invoke();
//
//        Render render = controller.getRender();
//        if (render instanceof ForwardActionRender) {
//            String actionUrl = ((ForwardActionRender) render).getActionUrl();
//            if (target.equals(actionUrl)) {
//                throw new RuntimeException("The forward action url is the same as before.");
//            } else {
//                handle(actionUrl, request, response, isHandled);
//            }
//        } else {
//            if (render == null && void.class != action.getMethod().getReturnType()
//                    && renderManager.getRenderFactory() instanceof JbootRenderFactory) {
//
//                JbootRenderFactory factory = (JbootRenderFactory) renderManager.getRenderFactory();
//                JbootReturnValueRender returnValueRender = factory.getReturnValueRender(action, invocation.getReturnValue());
//                String forwardTo = returnValueRender.getForwardTo();
//                if (forwardTo != null) {
//                    handle(getRealForwrdTo(forwardTo, target, action), request, response, isHandled);
//                    return;
//                } else {
//                    render = returnValueRender;
//                }
//            }
//
//            if (render == null) {
//                render = renderManager.getRenderFactory().getDefaultRender(action.getViewPath() + action.getMethodName());
//            }
//
//            render.setContext(request, response, action.getViewPath()).render();
//        }
//    }

    /**
     * 请求包装类用于将路径变量的URL中的额外参数加入request中
     */
    private class PathVariableWrappedRequest extends JbootServletRequestWrapper {
        private final Map<String, String[]> modifiableParameters;
        private Map<String, String[]> allParameters = null;

        public PathVariableWrappedRequest(HttpServletRequest request, HttpServletResponse response,
                                          Map<String, String> params) {
            super(request, response);
            modifiableParameters = new TreeMap<>();
            params.keySet().forEach(k -> {
                modifiableParameters.put(k, new String[]{params.get(k)});
            });
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            if (allParameters == null) {
                allParameters = new TreeMap<String, String[]>();
                allParameters.putAll(super.getParameterMap());
                allParameters.putAll(modifiableParameters);
            }
            return Collections.unmodifiableMap(allParameters);
        }

        @Override
        public String getParameter(final String name) {
            String[] strings = getParameterMap().get(name);
            if (strings != null) {
                return strings[0];
            }
            return super.getParameter(name);
        }
    }
}
