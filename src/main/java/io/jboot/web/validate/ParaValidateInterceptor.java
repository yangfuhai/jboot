/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.render.TextRender;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.fixedinterceptor.FixedInterceptor;

import java.lang.reflect.Method;

/**
 * 验证拦截器
 */
public class ParaValidateInterceptor implements FixedInterceptor {

    @Override
    public void intercept(Invocation inv) {

        Method method = inv.getMethod();

        EmptyValidate emptyParaValidate = method.getAnnotation(EmptyValidate.class);
        if (emptyParaValidate != null && !validateEmpty(inv, emptyParaValidate)) {
            return;
        }

        RegexValidate regexValidate = method.getAnnotation(RegexValidate.class);
        if (regexValidate != null && !validateRegex(inv, regexValidate)) {
            return;
        }

        CaptchaValidate captchaValidate = method.getAnnotation(CaptchaValidate.class);
        if (captchaValidate != null && !validateCaptache(inv, captchaValidate)) {
            return;
        }

        inv.invoke();
    }


    /**
     * 非空判断验证
     *
     * @param inv
     * @param emptyParaValidate
     * @return
     */
    private boolean validateEmpty(Invocation inv, EmptyValidate emptyParaValidate) {
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
                throw new IllegalArgumentException("@EmptyValidate not support form type : " + formType + ", " +
                        "see : io.jboot.web.controller.validate.FormType");
            }

            if (value == null || value.trim().length() == 0) {
                renderError(inv.getController()
                        , AnnotationUtil.get(emptyParaValidate.renderType())
                        , formName
                        , AnnotationUtil.get(form.message())
                        , AnnotationUtil.get(emptyParaValidate.redirectUrl())
                        , AnnotationUtil.get(emptyParaValidate.htmlPath())
                        , form.errorCode()
                );
                return false;
            }
        }

        return true;
    }


    /**
     * 正则验证
     *
     * @param inv
     * @param regexValidate
     * @return
     */
    private boolean validateRegex(Invocation inv, RegexValidate regexValidate) {
        RegexForm[] forms = regexValidate.value();
        if (ArrayUtil.isNullOrEmpty(forms)) {
            return true;
        }


        for (RegexForm form : forms) {
            String formName = AnnotationUtil.get(form.name());
            String formType = AnnotationUtil.get(form.type());
            if (StrUtil.isBlank(formName)) {
                throw new IllegalArgumentException("@MatchesForm.value must not be empty in " + inv.getController().getClass().getName() + "." + inv.getMethodName());
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
                throw new IllegalArgumentException("@MatchesValidate not support form type : " + formType + ", " +
                        "see : io.jboot.web.controller.validate.FormType");
            }

            if (value == null || !value.trim().matches(form.regex())) {
                renderError(inv.getController()
                        , AnnotationUtil.get(regexValidate.renderType())
                        , formName
                        , AnnotationUtil.get(form.message())
                        , AnnotationUtil.get(regexValidate.redirectUrl())
                        , AnnotationUtil.get(regexValidate.htmlPath())
                        , form.errorCode()
                );
                return false;
            }
        }

        return true;
    }


    /**
     * 对验证码进行验证
     *
     * @param inv
     * @param captchaValidate
     * @return
     */
    private boolean validateCaptache(Invocation inv, CaptchaValidate captchaValidate) {
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
                , AnnotationUtil.get(captchaValidate.htmlPath())
                , captchaValidate.errorCode()
        );

        return false;
    }


    private void renderError(Controller controller, String renderType, String formName, String message, String redirectUrl, String htmlPath, int errorCode) {
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
                    controller.renderError(403, new TextRender(reason));
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
