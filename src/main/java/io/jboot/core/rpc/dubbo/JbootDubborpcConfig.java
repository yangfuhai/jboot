/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.core.rpc.dubbo;

import io.jboot.config.annotation.PropertyConfig;


@PropertyConfig(prefix = "jboot.rpc.dubbo")
public class JbootDubborpcConfig {


    private String protocolName = "dubbo"; //default is dubbo
    private String protocolServer = "netty"; //default is netty
    private String protocolContextPath;
    private String protocolTransporter;
    private int protocolThreads = 200;

    private Boolean qosEnable = false;
    private Integer qosPort;
    private Boolean qosAcceptForeignIp;

    public String getProtocolName() {
        return protocolName;
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    public String getProtocolServer() {
        return protocolServer;
    }

    public void setProtocolServer(String protocolServer) {
        this.protocolServer = protocolServer;
    }

    public String getProtocolContextPath() {
        return protocolContextPath;
    }

    public void setProtocolContextPath(String protocolContextPath) {
        this.protocolContextPath = protocolContextPath;
    }

    public String getProtocolTransporter() {
        return protocolTransporter;
    }

    public void setProtocolTransporter(String protocolTransporter) {
        this.protocolTransporter = protocolTransporter;
    }

    public int getProtocolThreads() {
        return protocolThreads;
    }

    public void setProtocolThreads(int protocolThreads) {
        this.protocolThreads = protocolThreads;
    }

    public Boolean getQosEnable() {
        return qosEnable;
    }

    public void setQosEnable(Boolean qosEnable) {
        this.qosEnable = qosEnable;
    }

    public Integer getQosPort() {
        return qosPort;
    }

    public void setQosPort(Integer qosPort) {
        this.qosPort = qosPort;
    }

    public Boolean getQosAcceptForeignIp() {
        return qosAcceptForeignIp;
    }

    public void setQosAcceptForeignIp(Boolean qosAcceptForeignIp) {
        this.qosAcceptForeignIp = qosAcceptForeignIp;
    }
}
