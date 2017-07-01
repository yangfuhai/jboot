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
package io.jboot.component.shiro;

import io.jboot.config.annotation.PropertieConfig;

@PropertieConfig(prefix = "jboot.shiro")
public class JbootShiroConfig {

    private String loginUrl;
    private String successUrl;
    private String unauthorizedUrl;
    private String realm;
    private String authenticator;
    private String authorizer;
    private String subjectDAO;
    private String sessionManager;

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

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(String authenticator) {
        this.authenticator = authenticator;
    }

    public String getAuthorizer() {
        return authorizer;
    }

    public void setAuthorizer(String authorizer) {
        this.authorizer = authorizer;
    }

    public String getSubjectDAO() {
        return subjectDAO;
    }

    public void setSubjectDAO(String subjectDAO) {
        this.subjectDAO = subjectDAO;
    }

    public String getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(String sessionManager) {
        this.sessionManager = sessionManager;
    }


    private Boolean config;

    public boolean isConfigOK() {
        if (config != null) {
            return config;
        }

        config = false;

        try {
            Class.forName(realm);
            config = true;
        } catch (Throwable e) {
        }

        return config;
    }
}



