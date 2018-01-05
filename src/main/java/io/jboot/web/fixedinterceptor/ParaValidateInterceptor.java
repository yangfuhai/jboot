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
package io.jboot.web.fixedinterceptor;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.RequestUtils;
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.validate.EmptyValidate;
import io.jboot.web.controller.validate.Form;

/**
 * 验证拦截器
 */
public class ParaValidateInterceptor implements FixedInterceptor {

    public static final int DEFAULT_ERROR_CODE = 99;

    @Override
    public void intercept(FixedInvocation inv) {

        EmptyValidate emptyParaValidate = inv.getMethod().getAnnotation(EmptyValidate.class);
        if (emptyParaValidate == null) {
            inv.invoke();
            return;
        }

        Form[] forms = emptyParaValidate.value();
        if (ArrayUtils.isNullOrEmpty(forms)) {
            inv.invoke();
            return;
        }

        for (Form param : forms) {
            String value = inv.getController().getPara(param.value());
            if (value == null || value.trim().length() == 0) {
                renderError(inv.getController(), param.value(), param.message(), emptyParaValidate.errorRedirect());
                return;
            }
        }

        inv.invoke();
    }


    private void renderError(Controller controller, String form, String message, String errorRedirect) {

        message = StringUtils.isBlank(message) ? "数据不能为空" : message;

        if (StringUtils.isNotBlank(errorRedirect)) {
            if (controller instanceof JbootController) {
                JbootController c = (JbootController) controller;
                c.setFlashMap(Ret.fail("message", message).set("code", DEFAULT_ERROR_CODE).set("form", form));
            }
            controller.redirect(errorRedirect);
            return;
        }

        //如果ajax请求，返回一个错误数据。
        if (RequestUtils.isAjaxRequest(controller.getRequest())) {
            controller.renderJson(Ret.fail("message", message).set("code", DEFAULT_ERROR_CODE).set("form", form));
            return;
        }

        controller.renderError(404);
    }


}
