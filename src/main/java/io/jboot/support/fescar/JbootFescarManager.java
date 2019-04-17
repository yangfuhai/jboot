/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.support.fescar;


import com.alibaba.fescar.rm.GlobalLockTemplate;
import com.alibaba.fescar.rm.datasource.DataSourceProxy;
import com.alibaba.fescar.tm.api.DefaultFailureHandlerImpl;
import com.alibaba.fescar.tm.api.FailureHandler;
import com.alibaba.fescar.tm.api.TransactionalTemplate;
import io.jboot.Jboot;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import javax.sql.DataSource;

public class JbootFescarManager {

    private static JbootFescarManager fescarManager = new JbootFescarManager();

    private JbootFescarManager() {

    }

    public static JbootFescarManager me() {
        return fescarManager;
    }

    private FescarConfig config = Jboot.config(FescarConfig.class);
    private boolean enable = false;

    private TransactionalTemplate transactionalTemplate;
    private GlobalLockTemplate<Object> globalLockTemplate;
    private FescarGlobalTransactionManager transactionManager;


    public void init() {

        if (!config.isEnable()) {
            return;
        }

        if (!config.isConfigOk()) {
            throw new JbootIllegalConfigException("please config applicationId and txServiceGroup for fescar");
        }


        transactionManager = new FescarGlobalTransactionManager(
                config.getApplicationId(),
                config.getTxServiceGroup(),
                config.getMode()
        );
        transactionManager.init();

        this.transactionalTemplate = new TransactionalTemplate();
        this.globalLockTemplate = new GlobalLockTemplate<>();
        this.enable = true;
    }

    public boolean isEnable() {
        return enable;
    }

    public void stop() {
        if (isEnable()) transactionManager.destroy();
    }

    private Object handler;

    public FailureHandler getFailureHandler() {
        if (handler == null) {
            synchronized (this) {
                if (handler == null) {
                    String failureHandlerClassOrSpiName = config.getFailureHandler();
                    if (StrUtil.isBlank(failureHandlerClassOrSpiName)) {
                        handler = new DefaultFailureHandlerImpl();
                    } else {
                        if (failureHandlerClassOrSpiName.contains(".")) {
                            handler = ClassUtil.newInstance(failureHandlerClassOrSpiName);
                        }
                        if (handler == null) {
                            handler = JbootSpiLoader.load(FailureHandler.class, failureHandlerClassOrSpiName);
                        }
                        if (handler == null) {
                            handler = new DefaultFailureHandlerImpl();
                        }
                    }
                }
            }
        }
        return (FailureHandler) handler;
    }


    public TransactionalTemplate getTransactionalTemplate() {
        return transactionalTemplate;
    }

    public void setTransactionalTemplate(TransactionalTemplate transactionalTemplate) {
        this.transactionalTemplate = transactionalTemplate;
    }

    public GlobalLockTemplate<Object> getGlobalLockTemplate() {
        return globalLockTemplate;
    }

    public void setGlobalLockTemplate(GlobalLockTemplate<Object> globalLockTemplate) {
        this.globalLockTemplate = globalLockTemplate;
    }

    public DataSource wrapDataSource(DataSource dataSource) {
        return config.isEnable() && config.isConfigOk()
                ? new DataSourceProxy(dataSource)
                : dataSource;
    }


}
