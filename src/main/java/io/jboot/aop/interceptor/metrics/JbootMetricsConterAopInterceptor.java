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
package io.jboot.aop.interceptor.metrics;


import com.codahale.metrics.Counter;
import io.jboot.Jboot;
import io.jboot.component.metrics.EnableMetricsCounter;
import io.jboot.utils.StringUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 用于在AOP拦截，并通过Metrics的Conter进行统计
 */
public class JbootMetricsConterAopInterceptor implements MethodInterceptor {


    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        EnableMetricsCounter annotation = methodInvocation.getThis().getClass().getAnnotation(EnableMetricsCounter.class);

        String name = StringUtils.isBlank(annotation.value())
                ? methodInvocation.getThis().getClass().getName() + "." + methodInvocation.getMethod().getName()
                : annotation.value();

        Counter counter = Jboot.me().getMetrics().counter(name);
        try {
            counter.inc();
            return methodInvocation.proceed();
        } finally {
            counter.dec();
        }

    }
}
