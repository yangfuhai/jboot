package aop;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package aop
 */
public class AOP1Interceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        System.out.println("AOP1Interceptor invoked before:"+inv.getMethodName());
        inv.invoke();
        System.out.println("AOP1Interceptor invoked after:"+inv.getMethodName());
    }
}
