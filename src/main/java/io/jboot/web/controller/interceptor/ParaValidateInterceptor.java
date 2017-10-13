package io.jboot.web.controller.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.Ret;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.RequestUtils;
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.annotation.EmptyParaValidate;

/**
 * 验证拦截器
 */
public class ParaValidateInterceptor implements Interceptor {

    public static final int DEFAULT_ERROR_CODE = 99;

    @Override
    public void intercept(Invocation inv) {

        EmptyParaValidate emptyParaValidate = inv.getMethod().getAnnotation(EmptyParaValidate.class);
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


    private void renderError(Invocation inv, String param, String errorRedirect) {
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
