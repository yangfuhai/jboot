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
package io.jboot.support.jwt;

import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.utils.StrUtil;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@ConfigModel(prefix = "jboot.web.jwt")
public class JwtConfig {

    private String httpHeaderName = "Jwt";
    private String httpParameterKey;

    private String secret;

    /**
     * 有效期，单位毫秒，
     * 不配置时，或者 小于等于 0 时，永久有效
     */
    private long validityPeriod = 0;

    public String getHttpHeaderName() {
        return httpHeaderName;
    }

    public void setHttpHeaderName(String httpHeaderName) {
        this.httpHeaderName = httpHeaderName;
    }

    public String getHttpParameterKey() {
        return httpParameterKey;
    }

    public void setHttpParameterKey(String httpParameterKey) {
        this.httpParameterKey = httpParameterKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(long validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public boolean isConfigOk() {
        return StrUtil.isNotBlank(secret);
    }


}
