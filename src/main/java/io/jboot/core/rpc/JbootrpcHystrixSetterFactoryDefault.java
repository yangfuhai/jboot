/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.core.rpc;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import io.jboot.Jboot;

import java.lang.reflect.Method;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.component.hystrix
 */
public class JbootrpcHystrixSetterFactoryDefault implements JbootrpcHystrixSetterFactory {

    private static JbootrpcConfig config = Jboot.config(JbootrpcConfig.class);

    @Override
    public HystrixCommand.Setter createSetter(Object proxy, Method method, Object[] args) {

        String key = method.getDeclaringClass().getName() + "." + method.getName();
        HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey(key);

        HystrixCommand.Setter setter = HystrixCommand.Setter.withGroupKey(groupKey);

        //超时
        setter.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(config.getHystrixTimeout()));

        //线程池
        HystrixThreadPoolProperties.Setter threadPoolSetter = HystrixThreadPoolProperties.Setter();
        threadPoolSetter.withMaximumSize(config.getHystrixThreadPoolSize());
        threadPoolSetter.withMaxQueueSize(config.getHystrixThreadPoolSize());
        threadPoolSetter.withCoreSize(config.getHystrixThreadPoolSize());

        //threadPoolSetter 的其他设置，请自定义RPC的JbootrpcHystrixSetterFactory
        //jboot.rpc.hystrixSetterFactory = yourFactoryClass....

        setter.andThreadPoolPropertiesDefaults(threadPoolSetter);
        return setter;
    }
}
