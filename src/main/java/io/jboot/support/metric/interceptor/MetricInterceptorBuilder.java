/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.support.metric.interceptor;

import io.jboot.Jboot;
import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.Interceptors;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.support.metric.JbootMetricConfig;
import io.jboot.support.metric.annotation.*;

import java.lang.reflect.Method;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
@AutoLoad
public class MetricInterceptorBuilder implements InterceptorBuilder {

    private static JbootMetricConfig config = Jboot.config(JbootMetricConfig.class);

    @Override
    public void build(Class<?> targetClass, Method method, Interceptors interceptors) {
        if (!config.isConfigOk() || !config.isEnable()) {
            return;
        }

        if (Util.hasAnnotation(method, EnableMetricConcurrency.class)) {
            interceptors.add(MetricConcurrencyInterceptor.class);
        }

        if (Util.hasAnnotation(method, EnableMetricHistogram.class)) {
            interceptors.add(MetricHistogramInterceptor.class);
        }

        if (Util.hasAnnotation(method, EnableMetricCounter.class)) {
            interceptors.add(MetricCounterInterceptor.class);
        }

        if (Util.hasAnnotation(method, EnableMetricMeter.class)) {
            interceptors.add(MetricMeterInterceptor.class);
        }

        if (Util.hasAnnotation(method, EnableMetricTimer.class)) {
            interceptors.add(MetricTimerInterceptor.class);
        }

    }


}
