/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;

import java.util.Iterator;
import java.util.Map;

@Activate(group = Constants.CONSUMER)
public class JbootDubboConsumerTracingFilter implements Filter {


    @Override
    public Result invoke(Invoker<?> invoker, Invocation inv) throws RpcException {
        Tracer tracer = JbootOpentracingManager.me().getTracer();
        if (tracer == null) {
            return invoker.invoke(inv);
        }


        return processRefererTrace(tracer, invoker, inv);
    }


    protected Result processRefererTrace(Tracer tracer, Invoker<?> invoker, Invocation inv) {
        String operationName = JbootDubboTracingFilterKits.buildOperationName(invoker,inv);
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(operationName);
        Span activeSpan = JbootDubboTracingFilterKits.getActiveSpan();
        if (activeSpan != null) {
            spanBuilder.asChildOf(activeSpan);
        }
        Span span = spanBuilder.startManual();
//        span.setTag("requestId", request.getRequestId());

        attachTraceInfo(tracer, span, inv);
        return JbootDubboTracingFilterKits.process(invoker, inv, span);

    }


    protected void attachTraceInfo(Tracer tracer, Span span, final Invocation inv) {
        tracer.inject(span.context(), Format.Builtin.TEXT_MAP, new TextMap() {

            @Override
            public void put(String key, String value) {
                inv.getAttachments().put(key, value);
            }

            @Override
            public Iterator<Map.Entry<String, String>> iterator() {
                throw new UnsupportedOperationException("TextMapInjectAdapter should only be used with Tracer.inject()");
            }
        });
    }


}
