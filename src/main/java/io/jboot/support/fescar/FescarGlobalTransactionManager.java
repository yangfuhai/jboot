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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fescar.common.exception.NotSupportYetException;
import com.alibaba.fescar.config.ConfigurationFactory;
import com.alibaba.fescar.rm.RMClientAT;
import com.alibaba.fescar.tm.TMClient;
import com.alibaba.fescar.tm.api.DefaultFailureHandlerImpl;
import com.alibaba.fescar.tm.api.FailureHandler;

/***
 *
 * @author Hobbit Leon_wy@163.com
 *
 */
public class FescarGlobalTransactionManager {
    /**
     *
     */
    private String applicationId;
    private String txServiceGroup;

    private static final Logger LOGGER = LoggerFactory.getLogger(FescarGlobalTransactionManager.class);

    private static final int MT_MODE = 2;

    /**
     *
     */
    @SuppressWarnings("unused")
    private final FailureHandler failureHandlerHook;
    private static final FailureHandler DEFAULT_FAIL_HANDLER = new DefaultFailureHandlerImpl();
    private static final int AT_MODE = 1;
    private static final int DEFAULT_MODE = AT_MODE;
    private int mode;
    private final boolean disableGlobalTransaction = ConfigurationFactory.getInstance()
            .getBoolean("service.disableGlobalTransaction", false);

    /**
     * Instantiates a new Global transaction manager.
     *
     * @param txServiceGroup the tx service group
     */
    public FescarGlobalTransactionManager(String txServiceGroup) {
        this(txServiceGroup, txServiceGroup, DEFAULT_MODE);
    }

    /**
     * Instantiates a new Global transaction manager.
     *
     * @param txServiceGroup the tx service group
     * @param mode           the mode
     */
    public FescarGlobalTransactionManager(String txServiceGroup, int mode) {
        this(txServiceGroup, txServiceGroup, mode);
    }

    /**
     * Instantiates a new Global transaction manager.
     *
     * @param applicationId  the application id
     * @param txServiceGroup the default server group
     */
    public FescarGlobalTransactionManager(String applicationId, String txServiceGroup) {
        this(applicationId, txServiceGroup, DEFAULT_MODE);
    }

    /**
     * Instantiates a new Global transaction manager.
     *
     * @param applicationId  the application id
     * @param txServiceGroup the tx service group
     * @param mode           the mode
     */
    public FescarGlobalTransactionManager(String applicationId, String txServiceGroup, int mode) {
        this(applicationId, txServiceGroup, mode, DEFAULT_FAIL_HANDLER);
    }

    public FescarGlobalTransactionManager(String applicationId, String txServiceGroup,
                                          FailureHandler failureHandlerHook) {
        this(applicationId, txServiceGroup, DEFAULT_MODE, failureHandlerHook);
    }

    public FescarGlobalTransactionManager(String applicationId, String txServiceGroup, int mode,
                                          FailureHandler failureHandler) {
        this.applicationId = applicationId;
        this.txServiceGroup = txServiceGroup;
        this.mode = mode;
        this.failureHandlerHook = failureHandler;
    }

    private void initClient() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Initializing Global Transaction Clients ... ");
        }
        if (StringUtils.isEmpty(applicationId) || StringUtils.isEmpty(txServiceGroup)) {
            throw new IllegalArgumentException(
                    "applicationId: " + applicationId + ", txServiceGroup: " + txServiceGroup);
        }
        TMClient.init(applicationId, txServiceGroup);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Transaction Manager Client is initialized. applicationId[" + applicationId
                    + "] txServiceGroup[" + txServiceGroup + "]");
        }
        if ((AT_MODE & mode) > 0) {
            RMClientAT.init(applicationId, txServiceGroup);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Resource Manager for AT Client is initialized. applicationId[" + applicationId
                        + "] txServiceGroup[" + txServiceGroup + "]");
            }
        }
        if ((MT_MODE & mode) > 0) {
            throw new NotSupportYetException();
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Global Transaction Clients are initialized. ");
        }
    }

    public void init() {
        if (disableGlobalTransaction) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Global transaction is disabled.");
            }
            return;
        }
        initClient();
    }
}
