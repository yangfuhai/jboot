/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.db.tx;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.*;
import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.Interceptors;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Method;

/**
 * 缓存操作的拦截器
 *
 * @author michael yang
 */
@AutoLoad
public class TxEnableInterceptor implements Interceptor, InterceptorBuilder {


    @Override
    public void intercept(Invocation inv) {

        TxEnable txEnable = inv.getMethod().getAnnotation(TxEnable.class);
        String configName = AnnotationUtil.get(txEnable.config());

        DbPro dbPro = StrUtil.isBlank(configName) ? Db.use() : Db.use(configName);
        Config config = StrUtil.isBlank(configName) ? DbKit.getConfig() : DbKit.getConfig(configName);

        int transactionLevel = txEnable.transactionLevel();
        if (transactionLevel == -1) {
            transactionLevel = config.getTransactionLevel();
        }

        IAtom runnable = () -> {

            inv.invoke();

            //没有返回值的方法，只要没有异常就是提交事务
            if (inv.getMethod().getReturnType() == void.class) {
                return true;
            }

            Object result = inv.getReturnValue();

            if (result == null) {
                return false;
            }

            if (result instanceof Boolean) {
                return (boolean) result;
            }

            if (result instanceof Ret) {
                return ((Ret) result).isOk();
            }

            return true;
        };

        if (txEnable.inNewThread()) {
            try {
                dbPro.txInNewThread(transactionLevel, runnable).get();
            } catch (Exception e) {
                LogKit.error(e.toString(), e);
            }
        } else {
            dbPro.tx(transactionLevel, runnable);
        }

    }


    @Override
    public void build(Class<?> targetClass, Method method, Interceptors interceptors) {
        if (Util.hasAnnotation(method, TxEnable.class)) {
            interceptors.add(this);
        }
    }
}
