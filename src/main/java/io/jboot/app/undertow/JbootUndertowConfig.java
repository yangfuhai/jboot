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
package io.jboot.app.undertow;

import com.jfinal.server.undertow.PropExt;
import com.jfinal.server.undertow.UndertowConfig;
import com.jfinal.server.undertow.hotswap.HotSwapResolver;
import io.jboot.app.config.JbootConfigManager;

import java.io.IOException;
import java.net.ServerSocket;

public class JbootUndertowConfig extends UndertowConfig {

    protected static final String DEV_MODE = "undertow.devMode";
    protected static final String UNDERTOW_PORT = "undertow.port";
    protected static final String UNDERTOW_HOST = "undertow.host";
    protected static final String UNDERTOW_RESOURCEPATH = "undertow.resourcePath";


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

        PropExt propExt = super.createPropExt(undertowConfig)
                .append(new PropExt(JbootConfigManager.me().getProperties()));

        String port = propExt.get(UNDERTOW_PORT);

        //当不配置端口号时，默认为 8080
        if (port == null || port.trim().length() == 0) {
            propExt.getProperties().put(UNDERTOW_PORT, "8080");
            JbootConfigManager.setBootArg(UNDERTOW_PORT, "8080");
        }

        //当配置为 -1 或者 * 时，默认为随机端口号
        else if ((port.trim().equals("*") || port.trim().equals("-1"))) {
            Integer availablePort = getAvailablePort();
            if (availablePort != null) {
                propExt.getProperties().put(UNDERTOW_PORT, availablePort.toString());
                JbootConfigManager.setBootArg(UNDERTOW_PORT, availablePort.toString());
            }
        }

        //当不配置 undertow 开发模式时，默认开发模式为 devMode
        String devMode = propExt.get(DEV_MODE);
        if (devMode == null || devMode.trim().length() == 0) {
            propExt.getProperties().put(DEV_MODE, JbootConfigManager.me().isDevMode());
        }

        //当不配置 host 时，默认为 0.0.0.0
        String host = propExt.get(UNDERTOW_HOST);
        if (host == null || host.trim().length() == 0) {
            propExt.getProperties().put(UNDERTOW_HOST, "0.0.0.0");
            JbootConfigManager.setBootArg(UNDERTOW_HOST, "0.0.0.0");
        }

        //当不配置资源路径时，默认为 classpath 下的 webapp
        String resPath = propExt.get(UNDERTOW_RESOURCEPATH);
        if (resPath == null || resPath.trim().length() == 0) {
            propExt.getProperties().put(UNDERTOW_RESOURCEPATH, "classpath:webapp," + this.resourcePath);
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

    @Override
    public HotSwapResolver getHotSwapResolver() {
        if (hotSwapResolver == null) {
            hotSwapResolver = new JbootHotSwapResolver(getClassPathDirs());
            // 后续将此代码转移至 HotSwapResolver 中去，保持 UndertowConfig 的简洁
            if (hotSwapClassPrefix != null) {
                for (String prefix : hotSwapClassPrefix.split(",")) {
                    if (notBlank(prefix)) {
                        hotSwapResolver.addHotSwapClassPrefix(prefix);
                    }
                }
            }
        }
        return hotSwapResolver;
    }
}

