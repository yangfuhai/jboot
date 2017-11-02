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
package io.jboot.web.handler.inters;

import io.jboot.component.opentracing.EnableTracing;
import io.jboot.component.opentracing.JbootOpentracingManager;
import io.jboot.component.opentracing.JbootSpanContext;
import io.jboot.utils.StringUtils;
import io.jboot.web.handler.HandlerInterceptor;
import io.jboot.web.handler.HandlerInvocation;
import io.opentracing.Span;
import io.opentracing.Tracer;

/**
 * 用于对controller的自动注入
 * 注意：如果 Controller通过 @Clear 来把此 拦截器给清空，那么此方法（action）注入将会失效
 */
public class JbootCoreInterceptor implements HandlerInterceptor {


    @Override
    public void intercept(HandlerInvocation inv) {

        EnableTracing enableOpentracing = inv.getMethod().getAnnotation(EnableTracing.class);
        Tracer tracer = JbootOpentracingManager.me().getTracer();
        Span span = null;

        if (enableOpentracing != null && tracer != null) {
            String spanName = StringUtils.isBlank(enableOpentracing.value())
                    ? inv.getController().getClass().getName() + "." + inv.getMethodName()
                    : enableOpentracing.value();

            Tracer.SpanBuilder spanBuilder = tracer.buildSpan(spanName);

            span = spanBuilder.startManual();
            span.setTag("requestId", StringUtils.uuid());
            JbootSpanContext.init(span);
        }


        try {
            inv.invoke();
        } finally {
            if (span != null) {
                span.finish();
                JbootSpanContext.destroy();
            }
        }

    }


}
