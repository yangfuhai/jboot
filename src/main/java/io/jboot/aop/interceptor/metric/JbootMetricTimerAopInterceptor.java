/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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


import com.codahale.metrics.Timer;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.Jboot;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;
import io.jboot.support.metric.annotation.EnableMetricTimer;

/**
 * 用于在AOP拦截，并通过Metrics的Timer进行统计
 */
public class JbootMetricTimerAopInterceptor implements Interceptor {

    private static final String suffix = ".timer";

    @Override
    public void intercept(Invocation inv) {

        EnableMetricTimer annotation = inv.getMethod().getAnnotation(EnableMetricTimer.class);

        if (annotation == null) {
            inv.invoke();
            return;
        }

        Class targetClass = ClassUtil.getUsefulClass(inv.getTarget().getClass());

        String value = AnnotationUtil.get(annotation.value());
        String name = StrUtil.isBlank(value)
                ? targetClass + "." + inv.getMethod().getName() + suffix
                : value;

        Timer meter = Jboot.getMetric().timer(name);
        Timer.Context timerContext = meter.time();
        try {
            inv.invoke();
        } finally {
            timerContext.stop();
        }
    }
}
