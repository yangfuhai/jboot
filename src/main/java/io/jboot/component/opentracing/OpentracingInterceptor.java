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
package io.jboot.component.opentracing;

import io.jboot.utils.StringUtils;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.fixedinterceptor.HandlerInvocation;
import io.opentracing.Span;
import io.opentracing.Tracer;

public class OpentracingInterceptor implements FixedInterceptor {


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
            JbootSpanContext.add(span);
        }


        try {
            inv.invoke();
        } finally {
            if (span != null) {
                span.finish();
                JbootSpanContext.release();
            }
        }

    }


}
