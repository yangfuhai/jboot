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
package io.jboot.support.seata;

import com.jfinal.log.Log;
import io.seata.common.util.StringUtils;
import io.seata.config.ConfigurationFactory;
import io.seata.core.rpc.ShutdownHook;
import io.seata.core.rpc.netty.RmNettyRemotingClient;
import io.seata.core.rpc.netty.TmNettyRemotingClient;
import io.seata.rm.RMClient;
import io.seata.tm.TMClient;
import io.seata.tm.api.DefaultFailureHandlerImpl;
import io.seata.tm.api.FailureHandler;


public class SeataGlobalTransactionManager {

    @SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
    private static final Log LOGGER = Log.getLog(SeataGlobalTransactionManager.class);


    @SuppressWarnings("unused")
	private final FailureHandler failureHandlerHook;
    private static final FailureHandler DEFAULT_FAIL_HANDLER = new DefaultFailureHandlerImpl();
    private final boolean disableGlobalTransaction = ConfigurationFactory.getInstance()
            .getBoolean("service.disableGlobalTransaction", false);


    private static final int AT_MODE = 1;
    private static final int MT_MODE = 2;


    private static final int DEFAULT_MODE = AT_MODE + MT_MODE;
    @SuppressWarnings("unused")
	private static final int ORDER_NUM = 1024;


    private final String applicationId;
    private final String txServiceGroup;
    @SuppressWarnings("unused")
	private final int mode;


    /**
     * Instantiates a new Global transaction manager.
     *
     * @param txServiceGroup the tx service group
     */
    public SeataGlobalTransactionManager(String txServiceGroup) {
        this(txServiceGroup, txServiceGroup, DEFAULT_MODE);
    }

    /**
     * Instantiates a new Global transaction manager.
     *
     * @param txServiceGroup the tx service group
     * @param mode           the mode
     */
    public SeataGlobalTransactionManager(String txServiceGroup, int mode) {
        this(txServiceGroup, txServiceGroup, mode);
    }

    /**
     * Instantiates a new Global transaction manager.
     *
     * @param applicationId  the application id
     * @param txServiceGroup the default server group
     */
    public SeataGlobalTransactionManager(String applicationId, String txServiceGroup) {
        this(applicationId, txServiceGroup, DEFAULT_MODE);
    }

    /**
     * Instantiates a new Global transaction manager.
     *
     * @param applicationId  the application id
     * @param txServiceGroup the tx service group
     * @param mode           the mode
     */
    public SeataGlobalTransactionManager(String applicationId, String txServiceGroup, int mode) {
        this(applicationId, txServiceGroup, mode, DEFAULT_FAIL_HANDLER);
    }

    public SeataGlobalTransactionManager(String applicationId, String txServiceGroup,
                                          FailureHandler failureHandlerHook) {
        this(applicationId, txServiceGroup, DEFAULT_MODE, failureHandlerHook);
    }

    public SeataGlobalTransactionManager(String applicationId, String txServiceGroup, int mode,
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
        if (StringUtils.isNullOrEmpty(applicationId) || StringUtils.isNullOrEmpty(txServiceGroup)) {
            throw new IllegalArgumentException(
                    "applicationId: " + applicationId + ", txServiceGroup: " + txServiceGroup);
        }
        //init TM
        TMClient.init(applicationId, txServiceGroup);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(
                    "Transaction Manager Client is initialized. applicationId[" + applicationId + "] txServiceGroup["
                            + txServiceGroup + "]");
        }
        //init RM
        RMClient.init(applicationId, txServiceGroup);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Resource Manager is initialized. applicationId[" + applicationId + "] txServiceGroup[" + txServiceGroup + "]");
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Global Transaction Clients are initialized. ");
        }

        registerSpringShutdownHook();
    }


    private void registerSpringShutdownHook() {
        ShutdownHook.removeRuntimeShutdownHook();
        ShutdownHook.getInstance().addDisposable(TmNettyRemotingClient.getInstance(applicationId, txServiceGroup));
        ShutdownHook.getInstance().addDisposable(RmNettyRemotingClient.getInstance(applicationId, txServiceGroup));
    }

    public void destroy() {
        ShutdownHook.getInstance().destroyAll();
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
