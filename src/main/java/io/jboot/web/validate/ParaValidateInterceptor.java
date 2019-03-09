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
package io.jboot.web.validate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
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

        UrlParaValidate urlParaValidate = method.getAnnotation(UrlParaValidate.class);
        if (urlParaValidate != null && !validateUrlPara(inv, urlParaValidate)) {
            return;
        }


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

    private boolean validateUrlPara(FixedInvocation inv, UrlParaValidate urlParaValidate) {
        Controller controller = inv.getController();
        if (controller.getPara() != null) {
            return true;
        }


        renderError(inv.getController()
                , AnnotationUtil.get(urlParaValidate.renderType())
                , null
                , AnnotationUtil.get(urlParaValidate.message())
                , AnnotationUtil.get(urlParaValidate.redirectUrl())
                , AnnotationUtil.get(urlParaValidate.htmlPath()));

        return false;
    }


    /**
     * 对验证码进行验证
     *
     * @param inv
     * @param captchaValidate
     * @return
     */
    private boolean validateCaptache(FixedInvocation inv, CaptchaValidate captchaValidate) {
        String formName = AnnotationUtil.get(captchaValidate.form());
        if (StrUtil.isBlank(formName)) {
            throw new IllegalArgumentException("@CaptchaValidate.form must not be empty in " + inv.getController().getClass().getName() + "." + inv.getMethodName());
        }


        Controller controller = inv.getController();
        if (controller.validateCaptcha(formName)) {
            return true;
        }

        renderError(inv.getController()
                , AnnotationUtil.get(captchaValidate.renderType())
                , formName
                , AnnotationUtil.get(captchaValidate.message())
                , AnnotationUtil.get(captchaValidate.redirectUrl())
                , AnnotationUtil.get(captchaValidate.htmlPath()));

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
        if (ArrayUtil.isNullOrEmpty(forms)) {
            return true;
        }


        for (Form form : forms) {
            String formName = AnnotationUtil.get(form.name());
            String formType = AnnotationUtil.get(form.type());
            if (StrUtil.isBlank(formName)) {
                throw new IllegalArgumentException("@Form.value must not be empty in " + inv.getController().getClass().getName() + "." + inv.getMethodName());
            }
            String value = null;
            if (FormType.FORM_DATA.equalsIgnoreCase(formType)) {
                value = inv.getController().getPara(formName);
            } else if (FormType.RAW_DATA.equalsIgnoreCase(formType)) {
                try {
                    JSONObject json = JSON.parseObject(inv.getController().getRawData());
                    if (json != null) {
                        Object tmp = JSONPath.eval(json, "$." + formName);
                        if (tmp != null) {
                            value = tmp.toString();
                        }
                    }
                } catch (Exception e) {
                    value = null;
                }
            } else {
                throw new IllegalArgumentException("para validate not support form type : " + formType + ", " +
                        "see : io.jboot.web.controller.validate.FormType");
            }

            if (value == null || value.trim().length() == 0) {
                renderError(inv.getController()
                        , AnnotationUtil.get(emptyParaValidate.renderType())
                        , formName
                        , AnnotationUtil.get(form.message())
                        , AnnotationUtil.get(emptyParaValidate.redirectUrl())
                        , AnnotationUtil.get(emptyParaValidate.htmlPath()));
                return false;
            }
        }

        return true;
    }


    private void renderError(Controller controller, String renderType, String formName, String message, String redirectUrl, String htmlPath) {
        switch (renderType) {
            case ValidateRenderType.DEFAULT:
                if (RequestUtil.isAjaxRequest(controller.getRequest())) {
                    controller.renderJson(
                            Ret.fail("message", message)
                                    .set("code", DEFAULT_ERROR_CODE)
                                    .setIfNotNull("form", formName)
                    );
                } else {
                    controller.renderError(404);
                }
                break;
            case ValidateRenderType.JSON:
                controller.renderJson(
                        Ret.fail("message", message)
                                .set("code", DEFAULT_ERROR_CODE)
                                .setIfNotNull("form", formName)
                );
                break;
            case ValidateRenderType.REDIRECT:
                if (controller instanceof JbootController) {
                    ((JbootController) controller).setFlashAttr("message", message);
                }
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
