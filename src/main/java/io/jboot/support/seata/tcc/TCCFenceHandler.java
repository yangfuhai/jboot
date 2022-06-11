package io.jboot.support.seata.tcc;

/**
 * @author zhangxn
 * @date 2022/5/30  21:32
 */

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbPro;
import com.jfinal.plugin.activerecord.IAtom;
import io.jboot.db.datasource.DataSourceBuilder;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.db.datasource.DataSourceConfigManager;
import io.jboot.utils.StrUtil;
import io.seata.common.exception.FrameworkErrorCode;
import io.seata.common.thread.NamedThreadFactory;
import io.seata.rm.tcc.TwoPhaseResult;
import io.seata.rm.tcc.constant.TCCFenceConstant;
import io.seata.rm.tcc.exception.TCCFenceException;
import io.seata.rm.tcc.store.TCCFenceDO;
import io.seata.rm.tcc.store.TCCFenceStore;
import io.seata.rm.tcc.store.db.TCCFenceStoreDataBaseDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TCC Fence Handler(idempotent, non_rollback, suspend)
 *
 * @author kaka2code
 */
public class TCCFenceHandler {

    private TCCFenceHandler() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(io.seata.rm.tcc.TCCFenceHandler.class);

    private static final TCCFenceStore TCC_FENCE_DAO = TCCFenceStoreDataBaseDAO.getInstance();

    private static DataSource dataSource;


    private static final int MAX_THREAD_CLEAN = 1;

    private static final int MAX_QUEUE_SIZE = 500;

    private static final LinkedBlockingQueue<FenceLogIdentity> LOG_QUEUE = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

    private static FenceLogCleanRunnable fenceLogCleanRunnable;

    private static ExecutorService logCleanExecutor;

    static {
        try {
            initLogCleanExecutor();
        } catch (Exception e) {
            LOGGER.error("init fence log clean executor error", e);
        }
    }

    /**
     * tcc prepare method enhanced
     *
     * @param xid            the global transaction id
     * @param branchId       the branch transaction id
     * @param actionName     the action name
     * @return the boolean
     */
    public static Object prepareFence(String xid, Long branchId, String actionName) {
        DataSourceConfig dataSourceConfig = DataSourceConfigManager.me().getMainDatasourceConfig();
        DataSource dataSource = new DataSourceBuilder(dataSourceConfig).build();
        IAtom runnable = () -> {
            Connection connection = dataSource.getConnection();
            boolean result = insertTCCFenceLog(connection, xid, branchId, actionName, TCCFenceConstant.STATUS_TRIED);
            LOGGER.info("TCC fence prepare result: {}. xid: {}, branchId: {}", result, xid, branchId);
            if (!result) {
                throw new TCCFenceException(String.format("Insert tcc fence record error, prepare fence failed. xid= %s, branchId= %s", xid, branchId),
                        FrameworkErrorCode.InsertRecordError);
            }
            return result;
        };
        DbPro dbPro = StrUtil.isBlank(dataSourceConfig.getName()) ? Db.use() : Db.use(dataSourceConfig.getName());
        return dbPro.tx(runnable);
    }

    /**
     * tcc commit method enhanced
     *
     * @param commitMethod          commit method
     * @param targetTCCBean         target tcc bean
     * @param xid                   the global transaction id
     * @param branchId              the branch transaction id
     * @param args                  commit method's parameters
     * @return the boolean
     */
    public static boolean commitFence(Method commitMethod, Object targetTCCBean,
                                      String xid, Long branchId, Object[] args) {

        DataSourceConfig dataSourceConfig = DataSourceConfigManager.me().getMainDatasourceConfig();
        DataSource dataSource = new DataSourceBuilder(dataSourceConfig).build();
        IAtom runnable = () -> {
            Connection connection = dataSource.getConnection();
            TCCFenceDO tccFenceDO = TCC_FENCE_DAO.queryTCCFenceDO(connection, xid, branchId);
            if (tccFenceDO == null){
                throw new TCCFenceException(String.format("Insert tcc fence record error, rollback fence method failed. xid= %s, branchId= %s", xid, branchId),
                        FrameworkErrorCode.InsertRecordError);
            }
            if (TCCFenceConstant.STATUS_COMMITTED == tccFenceDO.getStatus()) {
                LOGGER.info("Branch transaction has already committed before. idempotency rejected. xid: {}, branchId: {}, status: {}", xid, branchId, tccFenceDO.getStatus());
                return true;
            }
            if (TCCFenceConstant.STATUS_ROLLBACKED == tccFenceDO.getStatus() || TCCFenceConstant.STATUS_SUSPENDED == tccFenceDO.getStatus()) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Branch transaction status is unexpected. xid: {}, branchId: {}, status: {}", xid, branchId, tccFenceDO.getStatus());
                }
                return false;
            }
            try {
                return updateStatusAndInvokeTargetMethod(connection, commitMethod, targetTCCBean, xid, branchId, TCCFenceConstant.STATUS_COMMITTED, args);
            } catch (Exception ex) {
                throw new TCCFenceException(ex.getCause());
            }
        };
        DbPro dbPro = StrUtil.isBlank(dataSourceConfig.getName()) ? Db.use() : Db.use(dataSourceConfig.getName());
        return dbPro.tx(runnable);
    }

    /**
     * tcc rollback method enhanced
     *
     * @param rollbackMethod        rollback method
     * @param targetTCCBean         target tcc bean
     * @param xid                   the global transaction id
     * @param branchId              the branch transaction id
     * @param args                  rollback method's parameters
     * @param actionName            the action name
     * @return the boolean
     */
    public static boolean rollbackFence(Method rollbackMethod, Object targetTCCBean,
                                        String xid, Long branchId, Object[] args, String actionName) {
        DataSourceConfig dataSourceConfig = DataSourceConfigManager.me().getMainDatasourceConfig();
        DataSource dataSource = new DataSourceBuilder(dataSourceConfig).build();
        IAtom runnable = () -> {
            try {
                Connection connection = dataSource.getConnection();
                TCCFenceDO tccFenceDO = TCC_FENCE_DAO.queryTCCFenceDO(connection, xid, branchId);
                if (tccFenceDO == null){
                    boolean result = insertTCCFenceLog(connection, xid, branchId, actionName, TCCFenceConstant.STATUS_SUSPENDED);
                    LOGGER.info("Insert tcc fence record result: {}. xid: {}, branchId: {}", result, xid, branchId);
                    if (!result) {
                        throw new TCCFenceException(String.format("Insert tcc fence record error, rollback fence method failed. xid= %s, branchId= %s", xid, branchId),
                                FrameworkErrorCode.InsertRecordError);
                    }
                    return true;
                } else {
                    if (TCCFenceConstant.STATUS_ROLLBACKED == tccFenceDO.getStatus() || TCCFenceConstant.STATUS_SUSPENDED == tccFenceDO.getStatus()) {
                        LOGGER.info("Branch transaction had already rollbacked before, idempotency rejected. xid: {}, branchId: {}, status: {}", xid, branchId, tccFenceDO.getStatus());
                        return true;
                    }
                    if (TCCFenceConstant.STATUS_COMMITTED == tccFenceDO.getStatus()) {
                        if (LOGGER.isWarnEnabled()) {
                            LOGGER.warn("Branch transaction status is unexpected. xid: {}, branchId: {}, status: {}", xid, branchId, tccFenceDO.getStatus());
                        }
                        return false;
                    }
                }
                return updateStatusAndInvokeTargetMethod(connection, rollbackMethod, targetTCCBean, xid, branchId, TCCFenceConstant.STATUS_ROLLBACKED,  args);
            } catch (Throwable ex) {
                throw new TCCFenceException(ex.getCause());
            }
        };
        DbPro dbPro = StrUtil.isBlank(dataSourceConfig.getName()) ? Db.use() : Db.use(dataSourceConfig.getName());
        return dbPro.tx(Connection.TRANSACTION_READ_UNCOMMITTED, runnable);
    }

    /**
     * Insert TCC fence log
     *
     * @param conn     the db connection
     * @param xid      the xid
     * @param branchId the branchId
     * @param status   the status
     * @return the boolean
     */
    private static boolean insertTCCFenceLog(Connection conn, String xid, Long branchId, String actionName, Integer status) {
        TCCFenceDO tccFenceDO = new TCCFenceDO();
        tccFenceDO.setXid(xid);
        tccFenceDO.setBranchId(branchId);
        tccFenceDO.setActionName(actionName);
        tccFenceDO.setStatus(status);
        return TCC_FENCE_DAO.insertTCCFenceDO(conn, tccFenceDO);
    }

    /**
     * Update TCC Fence status and invoke target method
     *
     * @param method                target method
     * @param targetTCCBean         target bean
     * @param xid                   the global transaction id
     * @param branchId              the branch transaction id
     * @param status                the tcc fence status
     * @return the boolean
     */
    private static boolean updateStatusAndInvokeTargetMethod(Connection conn, Method method, Object targetTCCBean,
                                                             String xid, Long branchId, int status, Object[] args) throws Exception {
        boolean result = TCC_FENCE_DAO.updateTCCFenceDO(conn, xid, branchId, status, TCCFenceConstant.STATUS_TRIED);
        if (result) {
            // invoke two phase method
            Object ret = method.invoke(targetTCCBean, args);
            if (null != ret) {
                if (ret instanceof TwoPhaseResult) {
                    result = ((TwoPhaseResult) ret).isSuccess();
                } else {
                    result = (boolean) ret;
                }
            }
        }
        return result;
    }

    private static void initLogCleanExecutor() {
        logCleanExecutor = new ThreadPoolExecutor(MAX_THREAD_CLEAN, MAX_THREAD_CLEAN, Integer.MAX_VALUE,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                new NamedThreadFactory("fenceLogCleanThread", MAX_THREAD_CLEAN, true)
        );
        fenceLogCleanRunnable = new FenceLogCleanRunnable();
        logCleanExecutor.submit(fenceLogCleanRunnable);
    }

    /**
     * Delete TCC Fence
     *
     * @param xid      the global transaction id
     * @param branchId the branch transaction id
     * @return the boolean
     */
    public static boolean deleteFence(String xid, Long branchId) {

        DataSourceConfig dataSourceConfig = DataSourceConfigManager.me().getMainDatasourceConfig();
        DataSource dataSource = new DataSourceBuilder(dataSourceConfig).build();
        IAtom runnable = () -> {
            try {
                Connection connection = dataSource.getConnection();
                boolean ret = TCC_FENCE_DAO.deleteTCCFenceDO(connection, xid, branchId);
                return ret;
            } catch (Throwable ex) {
                return false;
            }
        };
        DbPro dbPro = StrUtil.isBlank(dataSourceConfig.getName()) ? Db.use() : Db.use(dataSourceConfig.getName());
        return dbPro.tx(Connection.TRANSACTION_READ_UNCOMMITTED, runnable);
    }

    private static void addToLogCleanQueue(final String xid, final long branchId) {
        FenceLogIdentity logIdentity = new FenceLogIdentity();
        logIdentity.setXid(xid);
        logIdentity.setBranchId(branchId);
        try {
            LOG_QUEUE.add(logIdentity);
        } catch (Exception e) {
            LOGGER.warn("Insert tcc fence record into queue for async delete error,xid:{},branchId:{}", xid, branchId, e);
        }
    }

    /**
     * clean fence log that has the final status runnable.
     *
     * @see TCCFenceConstant
     */
    private static class FenceLogCleanRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                   FenceLogIdentity logIdentity = LOG_QUEUE.take();
                    boolean ret = deleteFence(logIdentity.getXid(), logIdentity.getBranchId());
                    if (!ret) {
                        LOGGER.error("delete fence log failed, xid: {}, branchId: {}", logIdentity.getXid(), logIdentity.getBranchId());
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("take fence log from queue for clean be interrupted", e);
                } catch (Exception e) {
                    LOGGER.error("exception occur when clean fence log", e);
                }
            }
        }
    }

    private static class FenceLogIdentity {
        /**
         * the global transaction id
         */
        private String xid;

        /**
         * the branch transaction id
         */
        private Long branchId;

        public String getXid() {
            return xid;
        }

        public Long getBranchId() {
            return branchId;
        }

        public void setXid(String xid) {
            this.xid = xid;
        }

        public void setBranchId(Long branchId) {
            this.branchId = branchId;
        }
    }
}
