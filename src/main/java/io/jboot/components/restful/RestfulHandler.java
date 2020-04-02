package io.jboot.components.restful;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Action;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import io.jboot.components.restful.annotation.ResponseHeader;
import io.jboot.components.restful.annotation.ResponseHeaders;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.handler.JbootActionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RestfulHandler extends JbootActionHandler {

    private static final Log log = Log.getLog(JbootActionHandler.class);

    @Override
    public Action getAction(String target, String[] urlPara, HttpServletRequest request) {
        Action action = super.getAction(target, urlPara);
        if (action == null) {
            if (action.getActionKey().equals("/") && action.getMethodName().equals("index")
                    && StrUtil.isNotBlank(target) && !target.equals(action.getActionKey())) {
                action = JbootRestfulManager.me().getRestfulAction(target, request.getMethod());
            }
        }


        return action;
    }

    @Override
    public Invocation getInvocation(Action action, Controller controller) {
        if (action instanceof RestfulAction) {
            Object[] args = RestfulUtils.parseActionMethodParameters(action.getActionKey(), action.getActionKey(),
                    action.getMethod(), controller.getRequest(), controller.getRawData());
            RestfulCallback restfulCallback = new RestfulCallback((RestfulAction) action, controller);
            return new Invocation(controller, action.getMethod(), action.getInterceptors(),
                    restfulCallback, args);
        } else {
            return super.getInvocation(action, controller);
        }
    }


    //    @Override
//    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
//        if (target.indexOf('.') != -1) {
//            return;
//        }
//
//        isHandled[0] = true;
//        String[] urlPara = {null};
//        Action action = getAction(target, urlPara);
//        RestfulAction restfulAction = null;
//        if (action != null && action.getActionKey().equals("/") && action.getMethodName().equals("index")
//                && StrUtil.isNotBlank(target) && !target.equals(action.getActionKey())) {
//            //如果被默认的"/"拦截，并且为index方法，但是请求的url又和actionKey不匹配，则有可能是restful请求
//            try {
//                restfulAction = JbootRestfulManager.me().getRestfulAction(target, request.getMethod());
//            } catch (RequestMethodErrorException e) {
//                handleActionException(target, request, response, action, e);
//                return;
//            }
//            if (restfulAction != null) {
//                action = null;
//            }
//        }
//        //如果无法从内置的action中获取action则尝试从restful管理的action中获取
//        if (action == null) {
//            // 尝试从restful 获取action
//            try {
//                if (restfulAction == null) {
//                    restfulAction = JbootRestfulManager.me().getRestfulAction(target, request.getMethod());
//                }
//            } catch (RequestMethodErrorException e) {
//                handleActionException(target, request, response, action, e);
//                return;
//            }
//            if (restfulAction != null) {
//                //restful 风格的请求处理
//                restfulRequest(restfulAction, target, request, response, isHandled);
//                return;
//            }
//            if (log.isWarnEnabled()) {
//                String qs = request.getQueryString();
//                log.warn("404 Action Not Found: " + (qs == null ? target : target + "?" + qs));
//            }
//            renderManager.getRenderFactory().getErrorRender(404).setContext(request, response).render();
//            return;
//        }
//        //在获取到了
//        super.handle(target, request, response, isHandled);
//    }

//    private void restfulRequest(RestfulAction restfulAction, String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
//        Action action = restfulAction.getAction();
//        Controller controller = null;
//        try {
//            controller = controllerFactory.getController(action.getControllerClass());
//            //注入依赖
//            if (injectDependency) {
//                com.jfinal.aop.Aop.inject(controller);
//            }
//            //绑定controller本身到当前线程
//            JbootControllerContext.hold(controller);
//            //初始化controller
//            CPI._init_(controller, action, request, response, null);
//
//            //解析参数
//            Object[] args = RestfulUtils.parseActionMethodParameters(target, restfulAction.getActionKey(),
//                    action.getMethod(), request, controller.getRawData());
//            RestfulCallback restfulCallback = new RestfulCallback(restfulAction, controller);
//
//            Invocation invocation = new Invocation(controller, action.getMethod(), action.getInterceptors(),
//                    restfulCallback, args);
//
//            RestfulInvocation fixedInvocation;
//            if (devMode) {
//                if (ActionReporter.isReportAfterInvocation(request)) {
//                    fixedInvocation = invokeInvocation(invocation);
//                    ActionReporter.report(target, controller, action);
//                } else {
//                    ActionReporter.report(target, controller, action);
//                    fixedInvocation = invokeInvocation(invocation);
//                }
//            } else {
//                fixedInvocation = invokeInvocation(invocation);
//            }
//
//            Object returnValue = fixedInvocation.getReturnValue();
//
//            // 判断是否带有@DownloadResponse
//            DownloadResponse downloadResponse = action.getMethod().getAnnotation(DownloadResponse.class);
//            if (returnValue == null && downloadResponse == null) {
//                //无返回结果的 restful 请求
//                controller.renderNull();
//            }
//            // 如果标记了@DownloadResponse，并且返回值不为空，会被认为自行处理了下载行为
//            if (downloadResponse != null) {
//                return;
//            }
//            if (returnValue != null) {
//                //初始化返回值
//                initRenderValue(returnValue, controller);
//            }
//
//            Render render = controller.getRender();
//            if (render instanceof ForwardActionRender) {
//                String actionUrl = ((ForwardActionRender) render).getActionUrl();
//                if (target.equals(actionUrl)) {
//                    throw new RuntimeException("The forward action url is the same as before.");
//                } else {
//                    handle(actionUrl, request, response, isHandled);
//                }
//                return;
//            }
//
//            if (render == null) {
//                render = renderManager.getRenderFactory().getDefaultRender(action.getViewPath() + action.getMethodName());
//            }
//
//            //初始化自定义头部
//            initResponseHeaders(response, fixedInvocation.getMethod());
//            //响应开始
//            render.setContext(request, response, action.getViewPath()).render();
//
//        } catch (RenderException | ParameterNullErrorException | ParameterParseErrorException | ActionException e) {
//            handleActionException(target, request, response, action, e);
//        } catch (Exception e) {
//            String info = ClassUtil.buildMethodString(action.getMethod());
//            if (log.isErrorEnabled()) {
//                String qs = request.getQueryString();
//                String targetInfo = qs == null ? target : target + "?" + qs;
//                log.error(info + " : " + targetInfo, e);
//            }
//            //自定义错误处理
//            handleActionException(target, request, response, action, e);
//        } finally {
//            JbootControllerContext.release();
//            controllerFactory.recycle(controller);
//        }
//    }

//    private void handleActionException(String target, HttpServletRequest request, HttpServletResponse response,
//                                       Action action, Exception e) {
//        RestfulErrorRender restfulErrorRender = JbootRestfulManager.me().getRestfulErrorRender();
//        restfulErrorRender.setContext(request, response, action == null ? "" : action.getViewPath());
//        restfulErrorRender.init(target, action, e);
//        restfulErrorRender.render();
//    }


//    protected RestfulInvocation invokeInvocation(Invocation inv) {
//        RestfulInvocation fixedInvocation = new RestfulInvocation(inv);
//        fixedInvocation.invoke();
//        return fixedInvocation;
//    }

    @Override
    public void setResponse(HttpServletResponse response, Action action) {
        ResponseHeader[] responseHeaders = action.getMethod().getAnnotationsByType(ResponseHeader.class);
        ResponseHeaders responseHeadersList = action.getMethod().getAnnotation(ResponseHeaders.class);
        if (responseHeadersList != null && responseHeadersList.value().length > 0) {
            if (responseHeaders != null && responseHeaders.length > 0) {
                responseHeaders = ArrayUtil.concat(responseHeaders, responseHeadersList.value());
            } else {
                responseHeaders = responseHeadersList.value();
            }
        }
        if (responseHeaders.length > 0) {
            for (ResponseHeader header : responseHeaders) {
                response.setHeader(header.key(), header.value());
            }
        }
    }

//    protected void initRenderValue(Object o, Controller controller) {
//        if (o.getClass().equals(String.class)
//                || o.getClass().equals(int.class)
//                || o.getClass().equals(double.class)
//                || o.getClass().equals(byte.class)
//                || o.getClass().equals(long.class)
//                || o.getClass().equals(float.class)
//                || o.getClass().equals(short.class)
//                || o.getClass().equals(char.class)
//                || o.getClass().equals(boolean.class)) {
//            controller.renderText(String.valueOf(o));
//        } else if (o.getClass().equals(File.class)) {
//            controller.renderFile((File) o);
//        } else if (o.getClass().equals(ResponseEntity.class)) {
//            ResponseEntity responseEntity = (ResponseEntity) o;
//            //设置自定义头部信息
//            Map<String, String> headers = responseEntity.getHeaders();
//
//            headers.forEach((k, v) -> controller.getResponse().setHeader(k, v));
//            //设置http状态代码
//            controller.getResponse().setStatus(responseEntity.getHttpStatus().value());
//            initRenderValue(responseEntity.getData(), controller);
//        } else if (o instanceof Render) { //如果是render类型直接设置render
//            controller.render((Render) o);
//        } else {
//            controller.renderJson(o);
//        }
//    }

}
