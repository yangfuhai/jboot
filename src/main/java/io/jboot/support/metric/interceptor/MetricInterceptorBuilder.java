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
package io.jboot.support.metric.interceptor;

import com.jfinal.aop.Interceptor;
import io.jboot.Jboot;
import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.support.metric.JbootMetricConfig;
import io.jboot.support.metric.annotation.*;

import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
@AutoLoad
public class MetricInterceptorBuilder implements InterceptorBuilder {

    private static JbootMetricConfig config = Jboot.config(JbootMetricConfig.class);

    @Override
    public void build(Class<?> serviceClass, Method method, LinkedList<Interceptor> interceptors) {
        if (!config.isConfigOk()) {
            return;
        }

        EnableMetricConcurrency concurrencyAnnotation = method.getAnnotation(EnableMetricConcurrency.class);
        if (concurrencyAnnotation != null) {
            interceptors.add(new MetricConcurrencyInterceptor());
        }

        EnableMetricHistogram histogramAnnotation = method.getAnnotation(EnableMetricHistogram.class);
        if (histogramAnnotation != null) {
            interceptors.add(new MetricHistogramInterceptor());
        }

        EnableMetricCounter counterAnnotation = method.getAnnotation(EnableMetricCounter.class);
        if (counterAnnotation != null) {
            interceptors.add(new MetricCounterInterceptor());
        }

        EnableMetricMeter meterAnnotation = method.getAnnotation(EnableMetricMeter.class);
        if (meterAnnotation != null) {
            interceptors.add(new MetricMeterInterceptor());
        }

        EnableMetricTimer timerAnnotation = method.getAnnotation(EnableMetricTimer.class);
        if (timerAnnotation != null) {
            interceptors.add(new MetricTimerInterceptor());
        }

    }


}
