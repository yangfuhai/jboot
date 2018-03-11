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
package io.jboot.component.jwt;

import com.jfinal.core.Controller;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.subject.WebSubject;

import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Jwt 和 shiro 链接的桥梁
 * @Description: 通过 Jwt 获取用户信息，再通过用户信息构建 shiro 的 subject
 * @Package io.jboot.component.jwt
 */
public interface JwtShiroBridge {

    public Subject buildSubject(Map jwtParas, Controller controller);

    public static final JwtShiroBridge demo = new JwtShiroBridge() {
        @Override
        public Subject buildSubject(Map jwtParas, Controller controller) {

            String userId = (String) jwtParas.get("userId");
            Object user = null; // userService.find(userId);
            PrincipalCollection principals = new SimplePrincipalCollection(user, "myRealmName");
            Subject.Builder builder = new WebSubject.Builder(controller.getRequest(), controller.getResponse());
            builder.principals(principals);
            builder.authenticated(true);
            return builder.buildSubject();
        }
    };
}
