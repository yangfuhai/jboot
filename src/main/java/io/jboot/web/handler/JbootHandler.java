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

import com.jfinal.aop.Interceptor;
import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.jfinal.log.Log;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import io.jboot.aop.JbootInjectManager;
import io.jboot.exception.JbootExceptionHolder;
import io.jboot.web.RequestManager;
import io.jboot.web.session.JbootServletRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;


public class JbootHandler extends Handler {
    static Log log = Log.getLog(JbootHandler.class);
    static String[] urlPara = {null};

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        if (target.indexOf('.') != -1) {
            return;
        }


        /**
         * 初始化 当前线程的 Hystrix
         */
        HystrixRequestContext context = HystrixRequestContext.initializeContext();


        /**
         * 通过 RequestManager 去保存 request，然后可以在当前线程的任何地方
         * 通过 RequestManager.me().getRequest() 去获取。
         */
        RequestManager.me().handle(request, response);

        /**
         * 初始化 异常记录器，用于记录异常信息，然后在页面输出
         */
        JbootExceptionHolder.init();


        try {

            /**
             * 执行请求逻辑
             */
            doHandle(target, new JbootServletRequestWrapper(request), response, isHandled);

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
        try {
            request.setAttribute("REQUEST", request);
            request.setAttribute("CPATH", request.getContextPath());
            injectInterceptors(target);
        } catch (Throwable ex) {
            log.error(ex.toString(), ex);
        } finally {
            next.handle(target, request, response, isHandled);
        }
    }

    /**
     * 对所有拦截器进行注入
     *
     * @param target
     */
    private void injectInterceptors(String target) {
        Action action = JFinal.me().getAction(target, urlPara);
        if (action == null) {
            return;
        }

        Interceptor[] interceptors = action.getInterceptors();
        if (interceptors == null || interceptors.length == 0) {
            return;
        }

        //如果注入过了，就没必要再次注入
        if (injectFlags.contains(target)) {
            return;
        }

        for (Interceptor interceptor : interceptors) {
            JbootInjectManager.me().getInjector().injectMembers(interceptor);
        }

        injectFlags.add(target);
    }

    /**
     * 用于记录拦截器是否被注入过，
     * 拦截器属于单例模式，注入过一次就没必要再次注入了
     */
    private static HashSet<String> injectFlags = new HashSet<>();


}
