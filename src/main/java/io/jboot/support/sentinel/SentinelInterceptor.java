/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.support.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/1/7
 */
public class SentinelInterceptor extends AbstractSentinelInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {

        SentinelResource annotation = inv.getMethod().getAnnotation(SentinelResource.class);
        if (annotation == null) {
            inv.invoke();
            return;
        }

        String resourceName = getResourceName(annotation.value(), inv.getMethod());
        EntryType entryType = annotation.entryType();
        int resourceType = annotation.resourceType();
        Entry entry = null;
        try {
            entry = SphU.entry(resourceName, resourceType, entryType, inv.getArgs());
            inv.invoke();
        } catch (BlockException ex) {
            try {
                inv.setReturnValue(handleBlockException(inv, annotation, ex));
            } catch (Throwable throwable) {
                if (inv.isActionInvocation()) {
                    SentinelUtil.blockRequest(inv.getController().getRequest(), inv.getController().getResponse());
                } else {
                    throwable.printStackTrace();
                }
            }
            return;
        } catch (Throwable ex) {
            Class<? extends Throwable>[] exceptionsToIgnore = annotation.exceptionsToIgnore();
            // The ignore list will be checked first.
            if (exceptionsToIgnore.length > 0 && exceptionBelongsTo(ex, exceptionsToIgnore)) {
                throw ex;
            }
            if (exceptionBelongsTo(ex, annotation.exceptionsToTrace())) {
                traceException(ex);
                try {
                    inv.setReturnValue(handleFallback(inv, annotation, ex));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return;
            }

            // No fallback function can handle the exception, so throw it out.
            throw ex;
        } finally {
            if (entry != null) {
                entry.exit(1, inv.getArgs());
            }
        }
    }


}
