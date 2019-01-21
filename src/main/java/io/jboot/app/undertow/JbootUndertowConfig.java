package io.jboot.app.undertow;

import com.jfinal.server.undertow.PropExt;
import com.jfinal.server.undertow.UndertowConfig;
import io.jboot.app.config.JbootConfigManager;

import java.io.IOException;
import java.net.ServerSocket;

public class JbootUndertowConfig extends UndertowConfig {


    public JbootUndertowConfig(Class<?> jfinalConfigClass) {
        super(jfinalConfigClass);
    }

    public JbootUndertowConfig(String jfinalConfigClass) {
        super(jfinalConfigClass);
    }

    public JbootUndertowConfig(Class<?> jfinalConfigClass, String undertowConfig) {
        super(jfinalConfigClass, undertowConfig);
    }

    public JbootUndertowConfig(String jfinalConfigClass, String undertowConfig) {
        super(jfinalConfigClass, undertowConfig);
    }

    @Override
    protected PropExt createPropExt(String undertowConfig) {

        this.resourcePath = "classpath:webapp," + this.resourcePath;

        PropExt propExt = super.createPropExt(undertowConfig)
                .append(new PropExt(JbootConfigManager.me().getProperties()));

        String port = propExt.get("undertow.port");
        Integer availablePort = getAvailablePort();

        if (port != null && port.trim().equals("*") && availablePort != null) {
            propExt.getProperties().put("undertow.port", availablePort.toString());
            JbootConfigManager.me().setBootArg("undertow.port", availablePort.toString());
        }

        return propExt;
    }

    /**
     * 获取随机可用的端口号
     *
     * @return
     */
    public static Integer getAvailablePort() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(0);
            return serverSocket.getLocalPort();
        } catch (IOException e) {
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

}

