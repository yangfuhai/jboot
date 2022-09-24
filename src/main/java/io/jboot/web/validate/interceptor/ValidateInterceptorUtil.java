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
public class ValidateInterceptorUtil {


    /**
     * 通过自定义的 ValidExceptionRetBuilder，可以自定义验证错误输出的 json 内容
     */
    private static ValidExceptionRetBuilder validExceptionRetBuilder = ValidExceptionRetBuilder.DEFAULT_BUILDER;

    public static ValidExceptionRetBuilder getValidExceptionRetBuilder() {
        return validExceptionRetBuilder;
    }

    public static void setValidExceptionRetBuilder(ValidExceptionRetBuilder validExceptionRetBuilder) {
        ValidateInterceptorUtil.validExceptionRetBuilder = validExceptionRetBuilder;
    }


    static String buildErrorMessage(Invocation inv, String annotation) {
        StringBuilder sb = new StringBuilder();
        sb.append("method \"").append(inv.getController().getClass().getName())
                .append(".")
                .append(inv.getMethodName())
                .append("()\"")
                .append(" has intercepted by annotation ")
                .append(annotation);
        return sb.toString();
    }


    static void renderValidException(Controller controller, String renderType, String formName, String message, String redirectUrl, String htmlPath, int errorCode) {
        String reason = StrUtil.isNotBlank(message) ? (formName + " validate failed: " + message) : (formName + " validate failed.");
        switch (renderType) {
            case ValidateRenderType.DEFAULT:
                if (RequestUtil.isAjaxRequest(controller.getRequest())
                        || RequestUtil.isJsonContentType(controller.getRequest())) {

                    Ret baseRet = Ret.fail("message", message)
                            .set("reason", reason)
                            .set("errorCode", errorCode)
                            .setIfNotNull("formName", formName);

                    Ret ret = validExceptionRetBuilder.build(baseRet);
                    controller.renderJson(ret);
                } else {
                    controller.renderText(reason);
                }
                break;
            case ValidateRenderType.JSON:
                Ret baseRet = Ret.fail("message", message)
                        .set("reason", reason)
                        .set("errorCode", errorCode)
                        .setIfNotNull("formName", formName);
                Ret ret = validExceptionRetBuilder.build(baseRet);
                controller.renderJson(ret);
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
                throw new IllegalArgumentException("Can not process render : " + renderType);
        }
    }


    public static interface ValidExceptionRetBuilder {

        ValidExceptionRetBuilder DEFAULT_BUILDER = ret -> ret;

        Ret build(Ret baseRet);
    }


}
