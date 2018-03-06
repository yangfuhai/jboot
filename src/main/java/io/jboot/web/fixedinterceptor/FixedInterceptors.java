/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import io.jboot.Jboot;
import io.jboot.component.jwt.JwtInterceptor;
import io.jboot.component.metric.JbootMetricInterceptor;
import io.jboot.component.opentracing.OpentracingInterceptor;
import io.jboot.component.shiro.JbootShiroInterceptor;
import io.jboot.web.controller.validate.ParaValidateInterceptor;
import io.jboot.web.cors.CORSInterceptor;
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
    private FixedInterceptor[] defaultInters = new FixedInterceptor[]{
            new LimitationInterceptor(),
            new CORSInterceptor(),
            new ParaValidateInterceptor(),
            new JwtInterceptor(),
            new JbootShiroInterceptor(),
            new OpentracingInterceptor(),
            new JbootMetricInterceptor()};

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
            Jboot.injectMembers(interceptor);
            allInters[i++] = interceptor;
        }

        for (FixedInterceptor interceptor : userInters) {
            Jboot.injectMembers(interceptor);
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
