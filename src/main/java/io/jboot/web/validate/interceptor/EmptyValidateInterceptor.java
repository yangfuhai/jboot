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
package io.jboot.web.validate.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.jfinal.aop.Invocation;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jboot.web.validate.FormType;

/**
 * 验证拦截器
 */
public class EmptyValidateInterceptor implements FixedInterceptor {

    private static final Log LOG = Log.getLog("Validate");

    @Override
    public void intercept(Invocation inv) {

        EmptyValidate emptyParaValidate = inv.getMethod().getAnnotation(EmptyValidate.class);
        if (emptyParaValidate != null && !validateEmpty(inv, emptyParaValidate)) {
            if (Jboot.isDevMode()){
                LOG.error(Util.buildErrorMessage(inv,"@EmptyValidate"));
            }
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
                Util.renderError(inv.getController()
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





}
