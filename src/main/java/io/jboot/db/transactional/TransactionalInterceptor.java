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
package io.jboot.db.transactional;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.*;
import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.Interceptors;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.aop.annotation.Transactional;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 缓存操作的拦截器
 *
 * @author michael yang
 */
@AutoLoad
public class TransactionalInterceptor implements Interceptor, InterceptorBuilder {


    @Override
    public void intercept(Invocation inv) {

        Transactional transactional = inv.getMethod().getAnnotation(Transactional.class);
        String configName = AnnotationUtil.get(transactional.config());

        DbPro dbPro = StrUtil.isBlank(configName) ? Db.use() : Db.use(configName);
        Config config = StrUtil.isBlank(configName) ? DbKit.getConfig() : DbKit.getConfig(configName);

        int transactionLevel = transactional.transactionLevel();
        if (transactionLevel == -1) {
            transactionLevel = config.getTransactionLevel();
        }


        IAtom runnable = () -> {
            try {
                inv.invoke();
            } catch (Throwable ex) {
                for (Class<? extends Throwable> forClass : transactional.noRollbackFor()) {
                    if (ex.getClass().isAssignableFrom(forClass)) {
                        LogKit.error(ex.toString(), ex);

                        //允许事务提交
                        return true;
                    }
                }
                throw ex;
            }

            //没有返回值的方法，只要没有异常就是提交事务
            if (inv.getMethod().getReturnType() == void.class) {
                return true;
            }

            Object result = inv.getReturnValue();

            if (result == null && transactional.rollbackForNull()) {
                return false;
            }

            if (result instanceof Boolean && !(Boolean) result && transactional.rollbackForFalse()) {
                return false;
            }

            if (result instanceof Ret && ((Ret) result).isFail() && transactional.rollbackForRetFail()) {
                return false;
            }

            return true;
        };


        if (transactional.inNewThread()) {
            try {
                Future<Boolean> future = txInNewThread(inv, transactional.threadPoolName(), dbPro, transactionLevel, runnable);

                //有返回值的场景下，需要等待返回值
                //或者没有返回值，但是配置了 @Transacional(threadWithBlocked=ture) 的时候
                if (inv.getMethod().getReturnType() != void.class || transactional.threadWithBlocked()) {
                    Boolean success = future.get();
                }
            } catch (Exception e) {
                LogKit.error(e.toString(), e);
            }
        } else {
            dbPro.tx(transactionLevel, runnable);
        }

    }


    public Future<Boolean> txInNewThread(Invocation inv, String name, DbPro dbPro, int transactionLevel, IAtom atom) {
        Callable<Boolean> callable = () -> dbPro.tx(transactionLevel, atom);
        return TransactionalManager.me().execute(inv, name, callable);
    }


    @Override
    public void build(Class<?> targetClass, Method method, Interceptors interceptors) {
        if (Util.hasAnnotation(method, Transactional.class)) {
            interceptors.add(this);
        }
    }
}
