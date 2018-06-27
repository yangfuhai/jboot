package aop;

import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.fixedinterceptor.FixedInvocation;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package aop
 */
public class FixedTestInterceptor implements FixedInterceptor {


    @Override
    public void intercept(FixedInvocation inv) {
        System.out.println("FixedTestInterceptor invoked before:"+inv.getMethodName());
        inv.invoke();
        System.out.println("FixedTestInterceptor invoked after:"+inv.getMethodName());
    }
}
