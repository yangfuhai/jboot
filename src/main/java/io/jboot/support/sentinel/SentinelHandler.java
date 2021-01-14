/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.alibaba.csp.sentinel.*;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.jfinal.handler.Handler;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SentinelHandler extends Handler {

    private static final String EMPTY_ORIGIN = "";

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        // 不对静态资源进行流量管理
        if (target.contains(".")){
            next.handle(target, request, response, isHandled);
            return;
        }

        Entry urlEntry = null;
        try {
            String targetResource = SentinelUtil.buildResource(request);

            if (StrUtil.isNotBlank(targetResource)) {
                ContextUtil.enter(targetResource, getOrigin(request));
                urlEntry = SphU.entry(targetResource, ResourceTypeConstants.COMMON_WEB, EntryType.IN);
            }

            next.handle(target, request, response, isHandled);
        } catch (BlockException e) {
            SentinelUtil.blockRequest(request, response);
            isHandled[0] = true;
        } catch (Exception e2) {
            Tracer.traceEntry(e2, urlEntry);
            throw e2;
        } finally {
            if (urlEntry != null) {
                urlEntry.exit();
            }
            ContextUtil.exit();
        }

    }

    protected String getOrigin(HttpServletRequest request) {
        return EMPTY_ORIGIN;
    }


}
