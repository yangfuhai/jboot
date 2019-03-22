package io.jboot.support.fescar;


import com.alibaba.fescar.tm.api.DefaultFailureHandlerImpl;
import com.alibaba.fescar.tm.api.FailureHandler;
import io.jboot.Jboot;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.ClassUtil;

public class FescarManager {

    private static FescarManager fescarManager = new FescarManager();

    private FescarManager() {

    }

    public static FescarManager me() {
        return fescarManager;
    }

    private FescarConfig config = Jboot.config(FescarConfig.class);
    private boolean enable = false;

    public void init() {

        if (!config.isEnable()) {
            return;
        }

        if (!config.isConfigOk()) {
            throw new JbootIllegalConfigException("please config applicationId and txServiceGroup for fescar");
        }


        FescarGlobalTransactionManager fgtm = new FescarGlobalTransactionManager(
                config.getApplicationId(),
                config.getTxServiceGroup(),
                config.getMode()
        );
        fgtm.init();

        this.enable = true;
    }

    public boolean isEnable() {
        return enable;
    }

    private FailureHandler handler = null;

    public FailureHandler getFailureHandler() {
        if (handler == null) {
            synchronized (this) {
                if (handler == null) {
                    String failureHandlerClassOrSpiName = config.getFailureHandler();

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

        return handler;
    }
}
