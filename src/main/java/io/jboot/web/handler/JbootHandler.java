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
package io.jboot.web.handler;

import com.jfinal.handler.Handler;
import com.jfinal.log.Log;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import io.jboot.exception.JbootExceptionHolder;
import io.jboot.web.RequestManager;
import io.jboot.web.session.JbootServletRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class JbootHandler extends Handler {
    static Log log = Log.getLog(JbootHandler.class);

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        if (target.indexOf('.') != -1) {
            return;
        }

        RequestManager.me().handle(request, response);
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        JbootExceptionHolder.init();
        try {
            doHandle(target, request, response, isHandled);
        } finally {
            try {
                context.shutdown();
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }

            try {
                RequestManager.me().release();
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }

            try {
                JbootExceptionHolder.release();
            } catch (Throwable ex) {
                log.error(ex.toString(), ex);
            }
        }

    }

    private void doHandle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        request.setAttribute("REQUEST", request);
        request.setAttribute("CPATH", request.getContextPath());
        next.handle(target, new JbootServletRequestWrapper(request), response, isHandled);
    }


}
