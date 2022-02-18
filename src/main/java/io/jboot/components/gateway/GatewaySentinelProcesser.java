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
package io.jboot.components.gateway;

import com.alibaba.csp.sentinel.*;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.jfinal.kit.LogKit;
import io.jboot.support.sentinel.SentinelUtil;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GatewaySentinelProcesser {


    public void process(GatewayHttpProxy proxy, String proxyUrl, JbootGatewayConfig config, HttpServletRequest req, HttpServletResponse resp, boolean skipExceptionRender) {
        Entry entry = null;
        String resourceName = SentinelUtil.buildResource(req);
        try {
            entry = SphU.entry(resourceName, ResourceTypeConstants.COMMON_API_GATEWAY, EntryType.IN);
            proxy.sendRequest(proxyUrl, req, resp);
        } catch (BlockException ex) {
            if (skipExceptionRender) {
                GatewayErrorRender errorRender = JbootGatewayManager.me().getGatewayErrorRender();
                if (errorRender != null) {
                    errorRender.renderError(ex, GatewayErrorRender.sentinelBlockedError, config, req, resp);
                } else {
                    processBlocked(config, req, resp);
                }
            } else {
                proxy.setException(ex);
            }
        } finally {
            if (proxy.getException() != null) {
                Tracer.traceEntry(proxy.getException(), entry);
            }
            if (entry != null) {
                entry.exit();
            }
        }
    }


    private void processBlocked(JbootGatewayConfig config, HttpServletRequest req, HttpServletResponse resp) {
        StringBuffer url = req.getRequestURL();

        if ("GET".equalsIgnoreCase(req.getMethod()) && StrUtil.isNotBlank(req.getQueryString())) {
            url.append("?").append(req.getQueryString());
        }

        try {
            if (StringUtil.isNotBlank(config.getSentinelBlockPage())) {
                String redirectUrl = config.getSentinelBlockPage() + "?http_referer=" + url.toString();
                resp.sendRedirect(redirectUrl);
            } else if (config.getSentinelBlockJsonMap() != null && !config.getSentinelBlockJsonMap().isEmpty()) {
                SentinelUtil.writeDefaultBlockedJson(resp, config.getSentinelBlockJsonMap());
            } else {
                SentinelUtil.writeDefaultBlockedPage(resp);
            }
        } catch (IOException ex) {
            LogKit.error(ex.toString(), ex);
        }
    }


}
