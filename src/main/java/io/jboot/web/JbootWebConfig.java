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
package io.jboot.web;

import io.jboot.app.config.JbootConfigManager;
import io.jboot.app.config.annotation.ConfigModel;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@ConfigModel(prefix = "jboot.web")
public class JbootWebConfig {

    public static final String DEFAULT_COOKIE_ENCRYPT_KEY = "JBOOT_DEFAULT_ENCRYPT_KEY";

    private String cookieEncryptKey = DEFAULT_COOKIE_ENCRYPT_KEY;
    private int cookieMaxAge = 60 * 60 * 24 * 2; // 2 days（单位：秒）
    private String webSocketEndpoint;
    private boolean escapeParasEnable = false;
    private boolean pathVariableEnable = false;

    public String getCookieEncryptKey() {
        return cookieEncryptKey;
    }

    public void setCookieEncryptKey(String cookieEncryptKey) {
        this.cookieEncryptKey = cookieEncryptKey;
    }

    public int getCookieMaxAge() {
        return cookieMaxAge;
    }

    public void setCookieMaxAge(int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    public String getWebSocketEndpoint() {
        return webSocketEndpoint;
    }

    public void setWebSocketEndpoint(String webSocketEndpoint) {
        this.webSocketEndpoint = webSocketEndpoint;
    }

    public boolean isEscapeParasEnable() {
        return escapeParasEnable;
    }

    public void setEscapeParasEnable(boolean escapeParasEnable) {
        this.escapeParasEnable = escapeParasEnable;
    }

    public boolean isPathVariableEnable() {
        return pathVariableEnable;
    }

    public void setPathVariableEnable(boolean pathVariableEnable) {
        this.pathVariableEnable = pathVariableEnable;
    }

    private static JbootWebConfig me;

    public static JbootWebConfig getInstance() {
        if (me == null) {
            me = JbootConfigManager.me().get(JbootWebConfig.class);
        }
        return me;
    }
}
