package io.jboot.support.fescar;

import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.utils.StrUtil;

@ConfigModel(prefix = "jboot.fescar")
public class FescarConfig {

    private boolean enable;
    private String applicationId;
    private String txServiceGroup;
    private String failureHandler;
    private int mode = 1;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getTxServiceGroup() {
        return txServiceGroup;
    }

    public void setTxServiceGroup(String txServiceGroup) {
        this.txServiceGroup = txServiceGroup;
    }

    public String getFailureHandler() {
        return failureHandler;
    }

    public void setFailureHandler(String failureHandler) {
        this.failureHandler = failureHandler;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isConfigOk() {
        return StrUtil.isNotBlank(applicationId) && StrUtil.isNotBlank(txServiceGroup);
    }
}
