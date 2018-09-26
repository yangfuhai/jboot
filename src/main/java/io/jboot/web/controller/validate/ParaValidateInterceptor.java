/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.controller.validate;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.RequestUtils;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.JbootController;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.fixedinterceptor.FixedInvocation;

import java.lang.reflect.Method;

/**
 * 验证拦截器
 */
public class ParaValidateInterceptor implements FixedInterceptor {

    public static final int DEFAULT_ERROR_CODE = 99;

    @Override
    public void intercept(FixedInvocation inv) {

        Method method = inv.getMethod();
        
        EmptyValidate emptyParaValidate = method.getAnnotation(EmptyValidate.class);
        if (emptyParaValidate != null && !validateEmpty(inv, emptyParaValidate)) {
            return;
        }

        CaptchaValidate captchaValidate = method.getAnnotation(CaptchaValidate.class);
        if (captchaValidate != null && !validateCaptache(inv, captchaValidate)) {
            return;
        }

        inv.invoke();

    }

    /**
     * 当 有文件上传的时候，需要通过 controller.getFiles() 才能正常通过 getParam 获取数据
     *
     * @param inv
     */
    private void parseMultpartRequestIfNecessary(FixedInvocation inv) {
        Controller controller = inv.getController();
        if (RequestUtils.isMultipartRequest(controller.getRequest())) {
            controller.getFiles();
        }
    }


    /**
     * 对验证码进行验证
     *
     * @param inv
     * @param captchaValidate
     * @return
     */
    private boolean validateCaptache(FixedInvocation inv, CaptchaValidate captchaValidate) {
        String formName = captchaValidate.form();
        if (StrUtils.isBlank(formName)) {
            throw new IllegalArgumentException("@CaptchaValidate.form must not be empty in " + inv.getController().getClass().getName() + "." + inv.getMethodName());
        }

        parseMultpartRequestIfNecessary(inv);

        Controller controller = inv.getController();
        if (controller.validateCaptcha(formName)) {
            return true;
        }

        switch (captchaValidate.renderType()) {
            case ValidateRenderType.DEFAULT:
                if (RequestUtils.isAjaxRequest(controller.getRequest())) {
                    controller.renderJson(Ret.fail("message", captchaValidate.flashMessage()).set("code", DEFAULT_ERROR_CODE).set("form", formName));
                } else {
                    controller.renderError(404);
                }
                break;
            case ValidateRenderType.JSON:
                controller.renderJson(Ret.fail("message", captchaValidate.message()).set("code", DEFAULT_ERROR_CODE).set("form", formName));
                break;
            case ValidateRenderType.REDIRECT:
                if (controller instanceof JbootController) {
                    ((JbootController) controller).setFlashAttr("message", captchaValidate.flashMessage());
                }
                controller.redirect(captchaValidate.message());
                break;
            case ValidateRenderType.RENDER:
                controller.render(captchaValidate.message());
                break;
            case ValidateRenderType.TEXT:
                controller.renderText(captchaValidate.message());
                break;
            default:
                throw new IllegalArgumentException("can not process render  : " + captchaValidate.renderType());
        }

        return false;
    }


    /**
     * 非空判断验证
     *
     * @param inv
     * @param emptyParaValidate
     * @return
     */
    private boolean validateEmpty(FixedInvocation inv, EmptyValidate emptyParaValidate) {
        Form[] forms = emptyParaValidate.value();
        if (ArrayUtils.isNullOrEmpty(forms)) {
            return true;
        }

        parseMultpartRequestIfNecessary(inv);

        for (Form form : forms) {
            String formName = form.name();
            if (StrUtils.isBlank(formName)) {
                throw new IllegalArgumentException("@Form.value must not be empty in " + inv.getController().getClass().getName() + "." + inv.getMethodName());
            }
            String value = inv.getController().getPara(formName);
            if (value == null || value.trim().length() == 0) {
                renderError(inv.getController(), formName, form.message(), emptyParaValidate);
                return false;
            }
        }

        return true;
    }


    private void renderError(Controller controller, String formName, String message, EmptyValidate emptyParaValidate) {
        switch (emptyParaValidate.renderType()) {
            case ValidateRenderType.DEFAULT:
                if (RequestUtils.isAjaxRequest(controller.getRequest())) {
                    controller.renderJson(Ret.fail("message", message).set("code", DEFAULT_ERROR_CODE).set("form", formName));
                } else {
                    controller.renderError(404);
                }
                break;
            case ValidateRenderType.JSON:
                controller.renderJson(Ret.fail("message", message).set("code", DEFAULT_ERROR_CODE).set("form", formName));
                break;
            case ValidateRenderType.REDIRECT:
                if (controller instanceof JbootController) {
                    ((JbootController) controller).setFlashAttr("message", message);
                }
                controller.redirect(emptyParaValidate.message());
                break;
            case ValidateRenderType.RENDER:
                controller.render(emptyParaValidate.message());
                break;
            case ValidateRenderType.TEXT:
                controller.renderText(emptyParaValidate.message());
                break;
            default:
                throw new IllegalArgumentException("can not process render : " + emptyParaValidate.renderType());
        }
    }


}
