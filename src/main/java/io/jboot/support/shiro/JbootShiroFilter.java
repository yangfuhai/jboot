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
package io.jboot.support.shiro;

import org.apache.shiro.web.env.WebEnvironment;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class JbootShiroFilter extends ShiroFilter {

    private int contextPathLength = 0;

    @Override
    public void init() throws Exception {
        WebEnvironment env = WebUtils.getRequiredWebEnvironment(getServletContext());

        if (env.getServletContext().getContextPath() != null) {
            contextPathLength = env.getServletContext().getContextPath().length();
        }

        setSecurityManager(env.getWebSecurityManager());

        FilterChainResolver resolver = env.getFilterChainResolver();
        if (resolver != null) {
            setFilterChainResolver(resolver);
        }
    }

    @Override
    protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String target = request.getRequestURI();
        if (contextPathLength != 0) {
            target = target.substring(contextPathLength);
        }

        if (target.indexOf('.') != -1) {
            chain.doFilter(request, response);
            return;
        }

        super.doFilterInternal(request, response, chain);
    }
}
