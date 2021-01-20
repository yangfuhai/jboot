/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.validate.CaptchaValidate;

/**
 * 验证拦截器
 */
public class CaptchaValidateInterceptor implements Interceptor {

    private static final Log LOG = Log.getLog("Validate");

    @Override
    public void intercept(Invocation inv) {

        CaptchaValidate captchaValidate = inv.getMethod().getAnnotation(CaptchaValidate.class);
        if (captchaValidate != null && !validateCaptache(inv, captchaValidate)) {
            if (Jboot.isDevMode()){
                LOG.error(Util.buildErrorMessage(inv,"@CaptchaValidate"));
            }
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
    private boolean validateCaptache(Invocation inv, CaptchaValidate captchaValidate) {
        String formName = AnnotationUtil.get(captchaValidate.form());
        if (StrUtil.isBlank(formName)) {
            throw new IllegalArgumentException("@CaptchaValidate.form must not be empty in " + inv.getController().getClass().getName() + "." + inv.getMethodName());
        }


        Controller controller = inv.getController();
        if (controller.validateCaptcha(formName)) {
            return true;
        }

        Util.renderValidException(inv.getController()
                , AnnotationUtil.get(captchaValidate.renderType())
                , formName
                , AnnotationUtil.get(captchaValidate.message())
                , AnnotationUtil.get(captchaValidate.redirectUrl())
                , AnnotationUtil.get(captchaValidate.htmlPath())
                , captchaValidate.errorCode()
        );

        return false;
    }




}
