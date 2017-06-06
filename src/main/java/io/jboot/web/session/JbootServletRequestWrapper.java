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
            /**
             * 没有启用缓存的话，就用系统自带的session
             */
            if (Jboot.getCache().isNoneCache()) {
                httpSession = super.getSession(create);
            } else {
                httpSession = new JbootHttpSessionWapper();
            }
        }

        return httpSession;


    }


}
