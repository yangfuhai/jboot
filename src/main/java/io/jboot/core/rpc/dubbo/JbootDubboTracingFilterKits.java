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
package io.jboot.core.rpc.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.jfinal.log.Log;
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
        return null;
    }

    public static void setActiveSpan(Span span) {
        RpcContext.getContext().set(ACTIVE_SPAN, span);
    }


}
