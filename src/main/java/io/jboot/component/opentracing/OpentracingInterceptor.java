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

import io.jboot.Jboot;
import io.jboot.utils.StringUtils;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.fixedinterceptor.FixedInvocation;
import io.opentracing.Span;
import io.opentracing.Tracer;

public class OpentracingInterceptor implements FixedInterceptor {

    private static JbootOpentracingConfig config = Jboot.config(JbootOpentracingConfig.class);

    @Override
    public void intercept(FixedInvocation inv) {

        if (!config.isConfigOk()) {
            inv.invoke();
            return;
        }


        Tracer tracer = JbootOpentracingManager.me().getTracer();
        if (tracer == null) {
            inv.invoke();
            return;
        }

        EnableTracing enableOpentracing = inv.getMethod().getAnnotation(EnableTracing.class);
        if (enableOpentracing == null) {
            inv.invoke();
            return;
        }

        String spanName = StringUtils.isBlank(enableOpentracing.value())
                ? inv.getController().getClass().getName() + "." + inv.getMethodName()
                : enableOpentracing.value();

        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(spanName);

        Span span = spanBuilder.startManual();

        span.setTag("requestId", StringUtils.uuid());
        JbootSpanContext.add(span);


        try {
            inv.invoke();
        } finally {
            span.finish();
            JbootSpanContext.release();
        }

    }


}
