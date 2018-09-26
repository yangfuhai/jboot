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
package io.jboot.aop.interceptor.metric;


import com.codahale.metrics.Meter;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.Jboot;
import io.jboot.component.metric.annotation.EnableMetricMeter;
import io.jboot.utils.ClassKits;
import io.jboot.utils.StrUtils;

/**
 * 用于在AOP拦截，并通过Metrics的Meter进行统计
 */
public class JbootMetricMeterAopInterceptor implements Interceptor {

    private static final String suffix = ".meter";

    @Override
    public void intercept(Invocation inv) {

        EnableMetricMeter annotation = inv.getMethod().getAnnotation(EnableMetricMeter.class);

        if (annotation == null) {
            inv.invoke();
            return;
        }

        Class targetClass = ClassKits.getUsefulClass(inv.getTarget().getClass());
        String name = StrUtils.isBlank(annotation.value())
                ? targetClass + "." + inv.getMethod().getName() + suffix
                : annotation.value();

        Meter meter = Jboot.me().getMetric().meter(name);
        meter.mark();
        inv.invoke();
    }
}
