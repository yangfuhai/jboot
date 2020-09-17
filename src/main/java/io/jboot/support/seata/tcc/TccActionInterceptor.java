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
package io.jboot.support.seata.tcc;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.support.seata.JbootSeataManager;


/**
 * TCC Interceptor
 * <p>
 * 参考： https://github.com/seata/seata/blob/develop/spring/src/main/java/io/seata/spring/tcc/TccActionInterceptor.java
 *
 * @author zhangsen
 */
public class TccActionInterceptor implements Interceptor {


    @Override
    public void intercept(Invocation inv) {

        if (!JbootSeataManager.me().isEnable()) {
            inv.invoke();
            return;
        }

        new TccActionProcesser().intercept(inv);
    }

}
