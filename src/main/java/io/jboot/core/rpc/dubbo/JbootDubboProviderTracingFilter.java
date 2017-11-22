/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.core.rpc.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import io.jboot.component.opentracing.JbootOpentracingManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;

@Activate(group = Constants.PROVIDER)
public class JbootDubboProviderTracingFilter implements Filter {


    @Override
    public Result invoke(Invoker<?> invoker, Invocation inv) throws RpcException {
        Tracer tracer = JbootOpentracingManager.me().getTracer();
        if (tracer == null) {
            return invoker.invoke(inv);
        }


        return processProviderTrace(tracer, invoker, inv);
    }


    protected Result processProviderTrace(Tracer tracer, Invoker<?> invoker, Invocation inv) {
        Span span = extractTraceInfo(tracer, invoker, inv);
//        span.setTag("requestId", request.getRequestId());
        JbootDubboTracingFilterKits.setActiveSpan(span);
        return JbootDubboTracingFilterKits.process(invoker, inv, span);
    }

    protected Span extractTraceInfo(Tracer tracer, Invoker<?> invoker, Invocation inv) {
        String operationName = JbootDubboTracingFilterKits.buildOperationName(invoker, inv);
        Tracer.SpanBuilder span = tracer.buildSpan(operationName);
        try {
            SpanContext spanContext = tracer.extract(Format.Builtin.TEXT_MAP, new TextMapExtractAdapter(inv.getAttachments()));
            if (spanContext != null) {
                span.asChildOf(spanContext);
            }
        } catch (Exception e) {
            span.withTag("Error", "extract from request fail, error msg:" + e.getMessage());
        }
        return span.startManual();
    }


}
