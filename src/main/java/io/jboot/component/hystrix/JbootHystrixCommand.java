/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.component.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;


public class JbootHystrixCommand extends HystrixCommand<Object> {

    private final HystrixRunnable runnable;

    public JbootHystrixCommand(String key, HystrixRunnable runnable) {
        super(HystrixCommandGroupKey.Factory.asKey(key));
        this.runnable = runnable;
    }

    public JbootHystrixCommand(HystrixCommandGroupKey group, HystrixRunnable runnable) {
        super(group);
        this.runnable = runnable;
    }

    public JbootHystrixCommand(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool, HystrixRunnable runnable) {
        super(group, threadPool);
        this.runnable = runnable;
    }

    public JbootHystrixCommand(HystrixCommandGroupKey group, int executionIsolationThreadTimeoutInMilliseconds, HystrixRunnable runnable) {
        super(group, executionIsolationThreadTimeoutInMilliseconds);
        this.runnable = runnable;
    }

    public JbootHystrixCommand(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool, int executionIsolationThreadTimeoutInMilliseconds, HystrixRunnable runnable) {
        super(group, threadPool, executionIsolationThreadTimeoutInMilliseconds);
        this.runnable = runnable;
    }

    public JbootHystrixCommand(Setter setter, HystrixRunnable runnable) {
        super(setter);
        this.runnable = runnable;
    }


    @Override
    protected Object run() {
        return runnable.run();
    }

    @Override
    protected Object getFallback() {
        return runnable.getFallback();
    }
}

