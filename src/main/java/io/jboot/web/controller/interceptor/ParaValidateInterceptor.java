package io.jboot.web.controller.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.EmptyParaValidate;

import java.lang.reflect.Method;

/**
 * 验证拦截器
 */
public class ParaValidateInterceptor implements Interceptor {

    public static final int DEFAULT_ERROR_CODE = 99;

    @Override
    public void intercept(Invocation inv) {
        Method method = inv.getMethod();

        EmptyParaValidate emptyParaValidate = method.getAnnotation(EmptyParaValidate.class);
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
        Controller controller = inv.getController();
        if (controller instanceof JbootController) {
            JbootController jc = (JbootController) controller;
            if (jc.isAjaxRequest()) {
                jc.renderJson(Kv.fail("msg", "数据不能为空").set("errorCode", DEFAULT_ERROR_CODE).set("field", param));
                return;
            }
        }
        controller.renderError(404);
    }


}
