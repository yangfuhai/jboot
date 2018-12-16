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


import com.codahale.metrics.Counter;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.Jboot;
import io.jboot.support.metric.annotation.EnableMetricConcurrency;
import io.jboot.kits.ClassKits;
import io.jboot.kits.StrUtils;

/**
 * 用于在AOP拦截，并通过Metrics的Conter进行统计
 */
public class JbootMetricConcurrencyAopInterceptor implements Interceptor {

    private static final String suffix = ".concurrency";

    @Override
    public void intercept(Invocation inv) {

        EnableMetricConcurrency annotation = inv.getMethod().getAnnotation(EnableMetricConcurrency.class);

        if (annotation == null) {
            inv.invoke();
            return;
        }

        Class targetClass = ClassKits.getUsefulClass(inv.getTarget().getClass());
        String name = StrUtils.isBlank(annotation.value())
                ? targetClass + "." + inv.getMethod().getName() + suffix
                : annotation.value();

        Counter counter = Jboot.me().getMetric().counter(name);
        try {
            counter.inc();
            inv.invoke();
        } finally {
            counter.dec();
        }
    }
}
