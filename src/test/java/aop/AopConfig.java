package aop;

import com.jfinal.config.Interceptors;
import io.jboot.server.listener.JbootAppListenerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package aop
 */
public class AopConfig extends JbootAppListenerBase {

    @Override
    public void onInterceptorConfig(Interceptors interceptors) {
        interceptors.addGlobalServiceInterceptor(new AOPInterceptor());
        System.out.println("onInterceptorConfig");
    }
}
