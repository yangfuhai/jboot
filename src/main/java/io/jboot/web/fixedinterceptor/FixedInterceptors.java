package io.jboot.web.fixedinterceptor;

import io.jboot.component.metrics.JbootMetricsInterceptor;
import io.jboot.component.opentracing.OpentracingInterceptor;
import io.jboot.component.shiro.JbootShiroInterceptor;
import io.jboot.web.limitation.LimitationInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.fixedinterceptor
 */
public class FixedInterceptors {

    private static final FixedInterceptors me = new FixedInterceptors();

    public static FixedInterceptors me() {
        return me;
    }


    /**
     * 默认的 Jboot 系统拦截器
     */
    private FixedInterceptor[] defaultInters = new FixedInterceptor[]{new LimitationInterceptor(), new OpentracingInterceptor(), new JbootMetricsInterceptor(), new JbootShiroInterceptor(), new ParaValidateInterceptor()};
    private List<FixedInterceptor> userInters = new ArrayList<>();


    private FixedInterceptor[] allInters = null;

    FixedInterceptor[] all() {
        if (allInters == null) {
            initInters();
        }
        return allInters;
    }


    private void initInters() {
        allInters = new FixedInterceptor[defaultInters.length + userInters.size()];

        int i = 0;
        for (FixedInterceptor interceptor : defaultInters) {
            allInters[i++] = interceptor;
        }

        for (FixedInterceptor interceptor : userInters) {
            allInters[i++] = interceptor;
        }
    }


    public void add(FixedInterceptor interceptor) {
        userInters.add(interceptor);
    }

    public List<FixedInterceptor> list() {
        return userInters;
    }
}
