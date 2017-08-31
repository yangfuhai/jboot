package io.jboot.config;

import io.jboot.config.annotation.PropertieConfig;

/**
 * 好吧，类名想了半天，Jboot配置的配置
 */
@PropertieConfig(prefix = "jboot.config")
public class JbootConfigConfig {

    /**
     * 是否启用远程配置
     */
    private boolean remoteEnable = false;

    /**
     * 远程配置的网址
     */
    private boolean remoteUrl;

    /**
     * 是否把本应用配置为远程配置的服务器
     */
    private boolean configServer = false;

    /**
     * 给远程提供的配置文件
     */
    private String configFile;


    public boolean isRemoteEnable() {
        return remoteEnable;
    }

    public void setRemoteEnable(boolean remoteEnable) {
        this.remoteEnable = remoteEnable;
    }

    public boolean isRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(boolean remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public boolean isConfigServer() {
        return configServer;
    }

    public void setConfigServer(boolean configServer) {
        this.configServer = configServer;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }
}
