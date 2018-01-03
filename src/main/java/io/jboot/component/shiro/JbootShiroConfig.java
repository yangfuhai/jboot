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
package io.jboot.component.shiro;

import com.jfinal.kit.PathKit;
import io.jboot.config.annotation.PropertyConfig;

import java.io.File;

@PropertyConfig(prefix = "jboot.shiro")
public class JbootShiroConfig {

    private String loginUrl;
    private String successUrl;
    private String unauthorizedUrl;
    private String shiroIniFile = "shiro.ini";


    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getUnauthorizedUrl() {
        return unauthorizedUrl;
    }

    public void setUnauthorizedUrl(String unauthorizedUrl) {
        this.unauthorizedUrl = unauthorizedUrl;
    }

    public String getShiroIniFile() {
        return shiroIniFile;
    }

    public void setShiroIniFile(String shiroIniFile) {
        this.shiroIniFile = shiroIniFile;
    }


    private Boolean config;

    public boolean isConfigOK() {
        if (config == null) {
            config = new File(PathKit.getRootClassPath(), shiroIniFile).exists();
        }
        return config;
    }
}



