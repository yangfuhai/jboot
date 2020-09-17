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

import com.codahale.metrics.Histogram;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.Jboot;
import io.jboot.support.metric.annotation.EnableMetricHistogram;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;

public class MetricHistogramInterceptor implements Interceptor {


    @Override
    public void intercept(Invocation inv) {

        EnableMetricHistogram histogramAnnotation = inv.getMethod().getAnnotation(EnableMetricHistogram.class);
        if (histogramAnnotation != null) {
            String value = AnnotationUtil.get(histogramAnnotation.value());
            String name = StrUtil.isBlank(value)
                    ? inv.getController().getClass().getName() + "." + inv.getMethodName() + ".histogram"
                    : value;


            Histogram histogram = Jboot.getMetric().histogram(name);
            histogram.update(histogramAnnotation.update());
        }

        inv.invoke();
    }


}
