/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.session;

import io.jboot.Jboot;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;


public class JbootServletRequestWrapper extends HttpServletRequestWrapper {

    HttpServletRequest originHttpServletRequest;
    HttpSession httpSession;

    public JbootServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.originHttpServletRequest = request;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);

    }


    @Override
    public HttpSession getSession(boolean create) {

        if (httpSession == null) {

            if (Jboot.me().getCache().isNoneCache()) {
                httpSession = new JbootDefaultSessionWapper();
            } else {
                httpSession = new JbootCacheSessionWapper();
            }
        }

        return httpSession;


    }


}
