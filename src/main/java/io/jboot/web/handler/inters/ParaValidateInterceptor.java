/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.web.handler.inters;

import com.jfinal.kit.Ret;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.RequestUtils;
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.validate.EmptyValidate;
import io.jboot.web.handler.HandlerInterceptor;
import io.jboot.web.handler.HandlerInvocation;

/**
 * 验证拦截器
 */
public class ParaValidateInterceptor implements HandlerInterceptor {

    public static final int DEFAULT_ERROR_CODE = 99;

    @Override
    public void intercept(HandlerInvocation inv) {

        EmptyValidate emptyParaValidate = inv.getMethod().getAnnotation(EmptyValidate.class);
        if (emptyParaValidate == null) {
            inv.invoke();
            return;
        }

        String[] paraKeys = emptyParaValidate.value();
        if (ArrayUtils.isNullOrEmpty(paraKeys)) {
            inv.invoke();
            return;
        }

        for (String param : paraKeys) {
            String value = inv.getController().getPara(param);
            if (value == null || value.trim().length() == 0) {
                renderError(inv, param, emptyParaValidate.errorRedirect());
                return;
            }
        }

        inv.invoke();
    }


    private void renderError(HandlerInvocation inv, String param, String errorRedirect) {
        if (StringUtils.isNotBlank(errorRedirect)) {
            inv.getController().redirect(errorRedirect);
            return;
        }

        //如果ajax请求，返回一个错误数据。
        if (RequestUtils.isAjaxRequest(inv.getController().getRequest())) {
            inv.getController().renderJson(Ret.fail("msg", "数据不能为空").set("errorCode", DEFAULT_ERROR_CODE).set("field", param));
            return;
        }

        inv.getController().renderError(404);
    }


}
