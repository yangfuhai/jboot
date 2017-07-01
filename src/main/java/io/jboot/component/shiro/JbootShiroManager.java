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


import io.jboot.Jboot;
import io.jboot.utils.ClassNewer;
import io.jboot.utils.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SubjectDAO;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;

/**
 * shiro 管理器
 */
public class JbootShiroManager {

    private static JbootShiroManager manager = new JbootShiroManager();
    private JbootShiroConfig config = Jboot.config(JbootShiroConfig.class);

    private JbootShiroManager() {
    }

    public static JbootShiroManager me() {
        return manager;
    }


    public void init() {
        if (!config.isConfigOK()) {
            // do nothing
            return;
        }

        SecurityManager securityManager = buildSecurityManager();
        SecurityUtils.setSecurityManager(securityManager);

        SecurityUtils.getSubject();
    }


    /**
     * 构建 SecurityManager
     *
     * @return 安全管理器
     */
    private SecurityManager buildSecurityManager() {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();

        //设置authenticator 认证器
        securityManager.setAuthenticator(buildAuthenticator());

        //设置authorizer 授权器
        securityManager.setAuthorizer(buildAuthorizer());

        //设置realm
        securityManager.setRealm(buildRealm());


        SubjectDAO subjectDAO = buildSubjectDAO();
        if (subjectDAO != null) {
            securityManager.setSubjectDAO(subjectDAO);
        }


        SessionManager sessionManager = buildSessionManager();
        if (sessionManager != null) {
            securityManager.setSessionManager(sessionManager);
        }


        return securityManager;
    }


    /**
     * 构建 realm ，用于对用户的查询
     *
     * @return
     */
    private Realm buildRealm() {
        Realm realm = ClassNewer.newInstance(config.getRealm());
        return realm;
    }

    /**
     * 构建 Authenticator 认证器，用于对用户的认证。
     *
     * @return
     */
    private Authenticator buildAuthenticator() {
        Authenticator authenticator = ClassNewer.newInstance(config.getAuthenticator());
        return authenticator;
    }


    /**
     * 构建 Authorizer 授权器，用于多用户进行授权。
     *
     * @return
     */
    private Authorizer buildAuthorizer() {
        Authorizer authorizer = ClassNewer.newInstance(config.getAuthorizer());
        return authorizer;
    }


    /**
     * 构建 SubjectDAO
     *
     * @return
     */
    private SubjectDAO buildSubjectDAO() {
        if (StringUtils.isBlank(config.getSubjectDAO())) {
            return null;
        }
        SubjectDAO subjectDAO = ClassNewer.newInstance(config.getSubjectDAO());
        return subjectDAO;
    }

    /**
     * 构建 SessionManager
     *
     * @return
     */
    private SessionManager buildSessionManager() {
        if (StringUtils.isBlank(config.getSessionManager())) {
            return null;
        }
        SessionManager sessionManager = ClassNewer.newInstance(config.getSessionManager());
        return sessionManager;
    }

}






