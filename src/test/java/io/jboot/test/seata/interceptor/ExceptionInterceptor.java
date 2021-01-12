package io.jboot.test.seata.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;
import io.jboot.web.controller.JbootController;

/**
 * @program: jboot
 * @description: ${description}
 * @author: zxn
 * @create: 2021-01-12 23:45
 **/
public class ExceptionInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation invocation) {
        invocation.invoke();
        JbootController jbootController = (JbootController) invocation.getController();
        if (StrKit.notBlank(jbootController.getPara("flag"))){
            int i = 1/0;
        }
    }
}
