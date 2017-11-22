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
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.jfinal.log.Log;
import io.jboot.component.opentracing.JbootSpanContext;
import io.opentracing.Span;

public class JbootDubboTracingFilterKits {


    static Log log = Log.getLog(JbootDubboTracingFilterKits.class);

    public static Result process(Invoker<?> invoker, Invocation inv, Span span) {
        Throwable ex = null;
        boolean exception = true;
        try {
            Result response = invoker.invoke(inv);
            if (response.getException() != null) {
                ex = response.getException();
            } else {
                exception = false;
            }
            return response;
        } catch (RuntimeException e) {
            ex = e;
            throw e;
        } finally {
            try {
                if (exception) {
                    span.log("request fail." + (ex == null ? "unknown exception" : ex.getMessage()));
                } else {
                    span.log("request success.");
                }
                span.finish();
            } catch (Exception e) {
                log.error("opentracing span finish error!", e);
            }
        }
    }

    public static String buildOperationName(Invoker<?> invoker, Invocation inv) {
        String version = invoker.getUrl().getParameter(Constants.VERSION_KEY);
        String group = invoker.getUrl().getParameter(Constants.GROUP_KEY);

        StringBuilder sn = new StringBuilder("Dubbo_");
        sn.append(group).append(":").append(version);
        sn.append("_");
        sn.append(invoker.getInterface().getName()).append(".");

        sn.append(inv.getMethodName());
        sn.append("(");
        Class<?>[] types = inv.getParameterTypes();
        if (types != null && types.length > 0) {
            boolean first = true;
            for (Class<?> type : types) {
                if (first) {
                    first = false;
                } else {
                    sn.append(",");
                }
                sn.append(type.getName());
            }
        }
        sn.append(") ");

        return sn.toString();
    }


    public static final String ACTIVE_SPAN = "ot_active_span";


    public static Span getActiveSpan() {
        Object span = RpcContext.getContext().get(ACTIVE_SPAN);
        if (span != null && span instanceof Span) {
            return (Span) span;
        }

        /**
         * 当通过 RpcContext 去获取不到的时候，有可能此线程 由于 hystrix 的原因，或其他原因，已经处于和RpcContext不同的线程
         * 所以通过 RpcContext 去获取不到当前的Span信息
         *
         * 在程序中，当启动新的线程进行操作的时候，会通过 JbootSpanContext.add(span) 来设置新线程的span内容
         */
        return JbootSpanContext.get();
    }

    public static void setActiveSpan(Span span) {
        RpcContext.getContext().set(ACTIVE_SPAN, span);
    }


}
