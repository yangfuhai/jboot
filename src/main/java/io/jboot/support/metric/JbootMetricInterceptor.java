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
package io.jboot.support.metric;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import io.jboot.Jboot;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;
import io.jboot.support.metric.annotation.*;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.fixedinterceptor.FixedInvocation;

/**
 * 用于对controller的Metrics 统计
 * 注意：如果 Controller通过 @Clear 来把此 拦截器给清空，那么此方法（action）注入将会失效
 */
public class JbootMetricInterceptor implements FixedInterceptor {

    private static JbootMetricConfig config = Jboot.config(JbootMetricConfig.class);

    @Override
    public void intercept(FixedInvocation inv) {

        if (!config.isConfigOk()) {
            inv.invoke();
            return;
        }


        Timer.Context timerContext = null;
        EnableMetricCounter counterAnnotation = inv.getMethod().getAnnotation(EnableMetricCounter.class);
        if (counterAnnotation != null) {
            String value = AnnotationUtil.get(counterAnnotation.value());
            String name = StrUtil.isBlank(value)
                    ? inv.getController().getClass().getName() + "." + inv.getMethodName() + ".counter"
                    : value;


            Counter counter = Jboot.getMetric().counter(name);
            counter.inc();
        }


        Counter concurrencyRecord = null;
        EnableMetricConcurrency concurrencyAnnotation = inv.getMethod().getAnnotation(EnableMetricConcurrency.class);
        if (concurrencyAnnotation != null) {
            String value = AnnotationUtil.get(concurrencyAnnotation.value());
            String name = StrUtil.isBlank(value)
                    ? inv.getController().getClass().getName() + "." + inv.getMethodName() + ".concurrency"
                    : value;


            concurrencyRecord = Jboot.getMetric().counter(name);
            concurrencyRecord.inc();
        }


        EnableMetricMeter meterAnnotation = inv.getMethod().getAnnotation(EnableMetricMeter.class);
        if (meterAnnotation != null) {
            String value = AnnotationUtil.get(meterAnnotation.value());
            String name = StrUtil.isBlank(value)
                    ? inv.getController().getClass().getName() + "." + inv.getMethodName() + ".meter"
                    : value;


            Meter meter = Jboot.getMetric().meter(name);
            meter.mark();
        }


        EnableMetricHistogram histogramAnnotation = inv.getMethod().getAnnotation(EnableMetricHistogram.class);
        if (histogramAnnotation != null) {
            String value = AnnotationUtil.get(histogramAnnotation.value());
            String name = StrUtil.isBlank(value)
                    ? inv.getController().getClass().getName() + "." + inv.getMethodName() + ".histogram"
                    : value;


            Histogram histogram = Jboot.getMetric().histogram(name);
            histogram.update(histogramAnnotation.update());
        }


        EnableMetricTimer timerAnnotation = inv.getMethod().getAnnotation(EnableMetricTimer.class);
        if (timerAnnotation != null) {
            String value = AnnotationUtil.get(timerAnnotation.value());
            String name = StrUtil.isBlank(value)
                    ? inv.getController().getClass().getName() + "." + inv.getMethodName() + ".timer"
                    : value;


            Timer timer = Jboot.getMetric().timer(name);
            timerContext = timer.time();
        }


        try {
            inv.invoke();
        } finally {
            if (concurrencyRecord != null) {
                concurrencyRecord.dec();
            }
            if (timerContext != null) {
                timerContext.stop();
            }
        }

    }


}
