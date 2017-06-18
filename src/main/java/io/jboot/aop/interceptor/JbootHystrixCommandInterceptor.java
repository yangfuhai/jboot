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


import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import io.jboot.component.hystrix.annotation.EnableHystrixCommand;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 用户Hystrix命令调用，方便Hystrix控制
 */
public class JbootHystrixCommandInterceptor implements MethodInterceptor {

    EnableHystrixCommand enableHystrixCommand;

    public JbootHystrixCommandInterceptor(EnableHystrixCommand hystrixCommand) {
        this.enableHystrixCommand = hystrixCommand;
    }

    public JbootHystrixCommandInterceptor() {

    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if (enableHystrixCommand == null) {
            enableHystrixCommand = methodInvocation.getThis().getClass().getAnnotation(EnableHystrixCommand.class);
        }

        JbootHystrixCommand command = new JbootHystrixCommand(new HystrixRunnable() {
            @Override
            public Object run() {
                try {
                    return methodInvocation.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return null;
            }
        }, enableHystrixCommand.key());


        return command.execute();
    }


    public static class JbootHystrixCommand extends HystrixCommand<Object> {

        private final HystrixRunnable runnable;

        public JbootHystrixCommand(HystrixRunnable runnable, String key) {
            super(HystrixCommandGroupKey.Factory.asKey(key));
            this.runnable = runnable;
        }

        @Override
        protected Object run() {
            return runnable.run();
        }
    }


    public static interface HystrixRunnable {
        public abstract Object run();
    }
}
