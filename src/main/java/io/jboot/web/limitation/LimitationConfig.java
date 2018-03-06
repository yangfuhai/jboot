/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.limitation;

import io.jboot.config.annotation.PropertyConfig;
import io.jboot.web.limitation.web.NoneAuthorizer;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.limitation
 */
@PropertyConfig(prefix = "jboot.limitation")
public class LimitationConfig {

    private int limitAjaxCode = 886;
    private String limitAjaxMessage = "request limit";

    private String limitView;

    private String webPath;
    private String webAuthorizer = NoneAuthorizer.class.getName();


    public int getLimitAjaxCode() {
        return limitAjaxCode;
    }

    public void setLimitAjaxCode(int limitAjaxCode) {
        this.limitAjaxCode = limitAjaxCode;
    }

    public String getLimitAjaxMessage() {
        return limitAjaxMessage;
    }

    public void setLimitAjaxMessage(String limitAjaxMessage) {
        this.limitAjaxMessage = limitAjaxMessage;
    }

    public String getLimitView() {
        return limitView;
    }

    public void setLimitView(String limitView) {
        this.limitView = limitView;
    }

    public String getWebPath() {
        return webPath;
    }

    public void setWebPath(String webPath) {
        this.webPath = webPath;
    }

    public String getWebAuthorizer() {
        return webAuthorizer;
    }

    public void setWebAuthorizer(String webAuthorizer) {
        this.webAuthorizer = webAuthorizer;
    }
}
