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
package io.jboot.test.web;

import com.jfinal.core.JFinalFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MockJFinalFilter extends JFinalFilter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        request.setCharacterEncoding(encoding);

        String target = request.getRequestURI();
        if (contextPathLength != 0) {
            target = target.substring(contextPathLength);
        }

        boolean[] isHandled = {false};
        try {
            handler.handle(target, request, response, isHandled);
        }
        catch (Exception e) {
            if (log.isErrorEnabled()) {
                String qs = request.getQueryString();
                log.error(qs == null ? target : target + "?" + qs, e);
            }
            throw new AssertionError(e.getMessage(),e);
        }

        if (isHandled[0] == false) {
            // 默认拒绝直接访问 jsp 文件，加固 tomcat、jetty 安全性
//            if (constants.getDenyAccessJsp() && isJsp(target)) {
//                com.jfinal.kit.HandlerKit.renderError404(request, response, isHandled);
//                return ;
//            }

            chain.doFilter(request, response);
        }
    }
}
