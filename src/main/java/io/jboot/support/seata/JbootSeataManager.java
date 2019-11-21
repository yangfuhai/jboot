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
package io.jboot.support.seata;


import io.jboot.Jboot;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;
import io.seata.rm.GlobalLockTemplate;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.tm.api.DefaultFailureHandlerImpl;
import io.seata.tm.api.FailureHandler;
import io.seata.tm.api.TransactionalTemplate;

import javax.sql.DataSource;

public class JbootSeataManager {

    private static JbootSeataManager seataManager = new JbootSeataManager();

    private JbootSeataManager() {

    }

    public static JbootSeataManager me() {
        return seataManager;
    }

    private SeataConfig config = Jboot.config(SeataConfig.class);
    private boolean enable = false;

    private TransactionalTemplate transactionalTemplate;
    private GlobalLockTemplate<Object> globalLockTemplate;
    private SeataGlobalTransactionManager transactionManager;


    public void init() {

        if (!config.isEnable()) {
            return;
        }

        if (!config.isConfigOk()) {
            throw new JbootIllegalConfigException("please config applicationId and txServiceGroup for seata");
        }


        transactionManager = new SeataGlobalTransactionManager(
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
        if (isEnable()) {
            transactionManager.destroy();
        }
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
