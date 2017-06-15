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
package io.jboot.aop.interceptor;


import com.codahale.metrics.Counter;
import io.jboot.Jboot;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * RPC拦截器，用于拦截调用者 和  被调用者
 */
public class JbootrpcInterceptor implements MethodInterceptor {


    public JbootrpcInterceptor() {

    }


    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        Counter counter = Jboot.getMetric().counter(methodInvocation.getThis().getClass() + "##" + methodInvocation.getMethod().getName());
        counter.inc();

        return methodInvocation.proceed();
    }
}
