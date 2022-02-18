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
package io.jboot.components.gateway.discovery;

import io.jboot.Jboot;
import io.jboot.utils.NetUtil;
import io.jboot.utils.StrUtil;

public class GatewayInstanceConfig {

    private String name;
    private String uriScheme = "http";
    private String uriHost;
    private int uriPort;
    private String uriPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUriScheme() {
        return uriScheme;
    }

    public void setUriScheme(String uriScheme) {
        this.uriScheme = uriScheme;
    }

    public String getUriHost() {
        return uriHost;
    }

    public void setUriHost(String uriHost) {
        this.uriHost = uriHost;
    }

    public int getUriPort() {
        return uriPort;
    }

    public void setUriPort(int uriPort) {
        this.uriPort = uriPort;
    }

    public String getUriPath() {
        return uriPath;
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }

    public String toUri() {
        StringBuilder sb = new StringBuilder(uriScheme).append("://");
        if (StrUtil.isNotBlank(uriHost)) {
            sb.append(uriHost);
        } else {
            sb.append(NetUtil.getLocalIpAddress());
        }
        if (uriPort == 0) {
            uriPort = Integer.parseInt(Jboot.configValue("undertow.port", "8080"));
        }
        sb.append(":").append(uriPort);
        if (StrUtil.isNotBlank(uriPath)) {
            sb.append(uriPath);
        }

        return sb.toString();
    }
}
