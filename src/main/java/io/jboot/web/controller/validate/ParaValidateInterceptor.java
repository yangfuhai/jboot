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
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.JbootController;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.fixedinterceptor.FixedInvocation;

/**
 * 验证拦截器
 */
public class ParaValidateInterceptor implements FixedInterceptor {

    public static final int DEFAULT_ERROR_CODE = 99;

    @Override
    public void intercept(FixedInvocation inv) {

        EmptyValidate emptyParaValidate = inv.getMethod().getAnnotation(EmptyValidate.class);
        if (emptyParaValidate != null && !validateEmpty(inv, emptyParaValidate)) {
            return;
        }

        CaptchaValidate captchaValidate = inv.getMethod().getAnnotation(CaptchaValidate.class);
        if (captchaValidate != null && !validateCaptache(inv, captchaValidate)) {
            return;
        }

        inv.invoke();
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
        if (StringUtils.isBlank(formName)) {
            throw new IllegalArgumentException("@CaptchaValidate.form must not be empty in " + inv.getController().getClass().getName() + "." + inv.getMethodName());
        }

        Controller controller = inv.getController();
        if (controller.validateCaptcha(formName)) {
            return true;
        }

        String errorRedirect = captchaValidate.errorRedirect();
        boolean isAjax = captchaValidate.isAjax();
        String message = StringUtils.isBlank(captchaValidate.message()) ? "验证码不能为空" : captchaValidate.message();
        if (!isAjax && StringUtils.isNotBlank(errorRedirect)) {
            if (controller instanceof JbootController) {
                JbootController c = (JbootController) controller;
                c.setFlashMap(Ret.fail("message", message).set("code", DEFAULT_ERROR_CODE).set("form", formName));
            }
            controller.redirect(errorRedirect);
            return false;
        }

        //如果ajax请求，返回一个错误数据。
        if (isAjax || RequestUtils.isAjaxRequest(controller.getRequest())) {
            controller.renderJson(Ret.fail("message", message).set("code", DEFAULT_ERROR_CODE).set("form", formName));
            return false;
        }

        controller.renderError(404);
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

        for (Form form : forms) {
            String formName = form.name();
            if (StringUtils.isBlank(formName)) {
                throw new IllegalArgumentException("@Form.value must not be empty in " + inv.getController().getClass().getName() + "." + inv.getMethodName());
            }
            String value = inv.getController().getPara(formName);
            if (value == null || value.trim().length() == 0) {
                renderError(inv.getController(), formName, form.message(), emptyParaValidate.errorRedirect(), emptyParaValidate.isAjax());
                return false;
            }
        }

        return true;
    }


    private void renderError(Controller controller, String form, String message, String errorRedirect, boolean isAjax) {

        message = StringUtils.isBlank(message) ? "数据不能为空" : message;

        if (!isAjax && StringUtils.isNotBlank(errorRedirect)) {
            if (controller instanceof JbootController) {
                JbootController c = (JbootController) controller;
                c.setFlashMap(Ret.fail("message", message).set("code", DEFAULT_ERROR_CODE).set("form", form));
            }
            controller.redirect(errorRedirect);
            return;
        }

        //如果ajax请求，返回一个错误数据。
        if (isAjax || RequestUtils.isAjaxRequest(controller.getRequest())) {
            controller.renderJson(Ret.fail("message", message).set("code", DEFAULT_ERROR_CODE).set("form", form));
            return;
        }

        controller.renderError(404);
    }


}
