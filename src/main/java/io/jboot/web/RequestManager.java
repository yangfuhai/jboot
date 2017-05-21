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
package io.jboot.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class RequestManager {
    private static RequestManager manager = new RequestManager();
    private ThreadLocal<HttpServletRequest> requests = new ThreadLocal<>();
    private ThreadLocal<HttpServletResponse> responses = new ThreadLocal<>();

    private RequestManager() {
    }

    public static RequestManager me() {
        return manager;
    }

    public void handle(HttpServletRequest req, HttpServletResponse response) {
        requests.set(req);
        responses.set(response);
    }

    public HttpServletRequest getRequest() {
        return requests.get();
    }

    public HttpServletResponse getResponse() {
        return responses.get();
    }

    public void release() {
        requests.remove();
        responses.remove();
    }


    public <T> T getRequestAttr(String key) {
        HttpServletRequest request = requests.get();
        if (request == null) {
            return null;
        }

        return (T) request.getAttribute(key);
    }

    public void setRequestAttr(String key, Object value) {
        HttpServletRequest request = requests.get();
        if (request == null) {
            return;
        }

        request.setAttribute(key, value);
    }

}
