/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.fixedinterceptor;

import com.jfinal.aop.Aop;
import io.jboot.components.limiter.LimiterInterceptor;
import io.jboot.support.jwt.JwtInterceptor;
import io.jboot.support.metric.JbootMetricInterceptor;
import io.jboot.support.seata.interceptor.SeataGlobalTransactionalInterceptor;
import io.jboot.support.seata.tcc.TccActionInterceptor;
import io.jboot.support.sentinel.SentinelInterceptor;
import io.jboot.support.shiro.JbootShiroInterceptor;
import io.jboot.web.validate.ValidateInterceptor;
import io.jboot.web.cors.CORSInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
    private FixedInterceptorWapper[] defaultInters = new FixedInterceptorWapper[]{
            new FixedInterceptorWapper(new SentinelInterceptor(), 9),
            new FixedInterceptorWapper(new LimiterInterceptor(), 10),
            new FixedInterceptorWapper(new CORSInterceptor(), 20),
            new FixedInterceptorWapper(new ValidateInterceptor(), 30),
            new FixedInterceptorWapper(new JwtInterceptor(), 40),
            new FixedInterceptorWapper(new JbootShiroInterceptor(), 50),
            new FixedInterceptorWapper(new JbootMetricInterceptor(), 60),
            new FixedInterceptorWapper(new SeataGlobalTransactionalInterceptor(), 80),
            new FixedInterceptorWapper(new TccActionInterceptor(), 90)
    };

    private List<FixedInterceptorWapper> userInters = new ArrayList<>();

    private FixedInterceptor[] allInters = null;

    private List<FixedInterceptorWapper> inters;

    FixedInterceptor[] all() {
        if (allInters == null) {
            synchronized (this) {
                if (allInters == null) {
                    initInters();
                }
            }
        }
        return allInters;
    }


    private void initInters() {

        FixedInterceptor[] interceptors = new FixedInterceptor[defaultInters.length + userInters.size()];
        inters = new ArrayList<>();
        inters.addAll(Arrays.asList(defaultInters));
        inters.addAll(userInters);
        inters.sort(Comparator.comparingInt(FixedInterceptorWapper::getOrderNo));

        int i = 0;
        for (FixedInterceptorWapper interceptor : inters) {
            interceptors[i++] = interceptor.getFixedInterceptor();
        }

        allInters = interceptors;
    }


    public void add(FixedInterceptor interceptor) {
        Aop.inject(interceptor);
        userInters.add(new FixedInterceptorWapper(interceptor));
    }

    public void add(FixedInterceptor interceptor, int orderNo) {
        if (orderNo < 0) {
            orderNo = 0;
        }
        Aop.inject(interceptor);
        userInters.add(new FixedInterceptorWapper(interceptor, orderNo));
    }

    public List<FixedInterceptorWapper> list() {
        return inters;
    }
}
