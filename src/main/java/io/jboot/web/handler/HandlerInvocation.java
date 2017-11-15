package io.jboot.web.handler;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jboot.web.handler.inters.JbootCoreInterceptor;
import io.jboot.web.handler.inters.JbootMetricsInterceptor;
import io.jboot.web.handler.inters.JbootShiroInterceptor;
import io.jboot.web.handler.inters.ParaValidateInterceptor;

import java.lang.reflect.Method;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.handler
 */
public class HandlerInvocation {

    private Invocation invocation;


    private static HandlerInterceptor[] inters = new HandlerInterceptor[]{new JbootCoreInterceptor(), new JbootMetricsInterceptor(), new JbootShiroInterceptor(), new ParaValidateInterceptor()};
    private int index = 0;


    public HandlerInvocation(Invocation invocation) {
        this.invocation = invocation;
    }


    public void invoke() {
        if (index < inters.length) {
            inters[index++].intercept(this);
        } else if (index++ == inters.length) {    // index++ ensure invoke action only one time
            invocation.invoke();
        }
    }


    public Method getMethod() {
        return invocation.getMethod();
    }

    public Controller getController() {
        return invocation.getController();
    }

    public String getActionKey() {
        return invocation.getActionKey();
    }

    public String getControllerKey() {
        return invocation.getControllerKey();
    }

    public String getMethodName() {
        return invocation.getMethodName();
    }


}
