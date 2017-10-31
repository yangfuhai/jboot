/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.component.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.Jboot;
import io.jboot.utils.StringUtils;

/**
 * 用于对controller的Metrics 统计
 * 注意：如果 Controller通过 @Clear 来把此 拦截器给清空，那么此方法（action）注入将会失效
 */
public class JbootMetricsInterceptor implements Interceptor {


    @Override
    public void intercept(Invocation inv) {


        Counter counter = null;
        Timer.Context timerContext = null;


        EnableMetricsCounter counterAnnotation = inv.getMethod().getAnnotation(EnableMetricsCounter.class);
        if (counterAnnotation != null) {
            String name = StringUtils.isBlank(counterAnnotation.value())
                    ? inv.getController().getClass().getName() + "." + inv.getMethodName()
                    : counterAnnotation.value();


            counter = Jboot.me().getMetrics().counter(name);
            counter.inc();
        }


        EnableMetricsMeter meterAnnotation = inv.getMethod().getAnnotation(EnableMetricsMeter.class);
        if (meterAnnotation != null) {
            String name = StringUtils.isBlank(meterAnnotation.value())
                    ? inv.getController().getClass().getName() + "." + inv.getMethodName()
                    : meterAnnotation.value();


            Meter meter = Jboot.me().getMetrics().meter(name);
            meter.mark();
        }


        EnableMetricsHistogram histogramAnnotation = inv.getMethod().getAnnotation(EnableMetricsHistogram.class);
        if (histogramAnnotation != null) {
            String name = StringUtils.isBlank(histogramAnnotation.value())
                    ? inv.getController().getClass().getName() + "." + inv.getMethodName()
                    : histogramAnnotation.value();


            Histogram histogram = Jboot.me().getMetrics().histogram(name);
            histogram.update(histogramAnnotation.update());
        }


        EnableMetricsTimer timerAnnotation = inv.getMethod().getAnnotation(EnableMetricsTimer.class);
        if (timerAnnotation != null) {
            String name = StringUtils.isBlank(timerAnnotation.value())
                    ? inv.getController().getClass().getName() + "." + inv.getMethodName()
                    : timerAnnotation.value();


            Timer timer = Jboot.me().getMetrics().timer(name);
            timerContext = timer.time();
        }


        try {
            inv.invoke();
        } finally {
            if (counter != null) {
                counter.dec();
            }
            if (timerContext != null) {
                timerContext.stop();
            }
        }

    }


}
