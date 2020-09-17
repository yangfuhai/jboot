package io.jboot.web.validate.interceptor;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.validate.ValidateRenderType;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
class Util {


    static String buildErrorMessage(Invocation inv, String annotation){
        StringBuilder sb = new StringBuilder();
        sb.append("method \"").append(inv.getController().getClass().getName())
                .append(".")
                .append(inv.getMethodName())
                .append("()\"")
                .append(" has intercepted by annotation ")
                .append(annotation);
        return sb.toString();
    }



    static void renderError(Controller controller, String renderType, String formName, String message, String redirectUrl, String htmlPath, int errorCode) {
        String reason = StrUtil.isNotBlank(message) ? (formName + " validate failed: " + message) : (formName + " validate failed!");
        switch (renderType) {
            case ValidateRenderType.DEFAULT:
                if (RequestUtil.isAjaxRequest(controller.getRequest())) {
                    controller.renderJson(
                            Ret.fail("message", message)
                                    .set("reason", reason)
                                    .set("errorCode", errorCode)
                                    .setIfNotNull("formName", formName)
                    );
                } else {
                    controller.renderText(reason);
                }
                break;
            case ValidateRenderType.JSON:
                controller.renderJson(
                        Ret.fail("message", message)
                                .set("reason", reason)
                                .set("errorCode", errorCode)
                                .setIfNotNull("formName", formName)
                );
                break;
            case ValidateRenderType.REDIRECT:
                controller.redirect(redirectUrl);
                break;
            case ValidateRenderType.HTML:
                controller.render(htmlPath);
                break;
            case ValidateRenderType.TEXT:
                controller.renderText(message);
                break;
            default:
                throw new IllegalArgumentException("can not process render : " + renderType);
        }
    }


}
