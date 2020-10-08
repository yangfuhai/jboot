/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.aop.cglib;

import com.jfinal.proxy.ProxyFactory;

/**
 * JbootCglibProxyFactory 用于扩展 cglib 的代理模式
 *
 * <pre>
 * 配置方法：
 * public void configConstant(Constants me) {
 *     ProxyManager.me().setProxyFactory(new JbootCglibProxyFactory());
 * }
 * </pre>
 */
public class JbootCglibProxyFactory extends ProxyFactory {

    @Override
    public <T> T get(Class<T> target) {
        return (T) net.sf.cglib.proxy.Enhancer.create(target, new JbootCglibCallback());
    }


}



