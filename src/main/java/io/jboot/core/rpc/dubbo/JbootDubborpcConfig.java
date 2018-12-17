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

import com.alibaba.dubbo.config.ProtocolConfig;
import io.jboot.core.config.annotation.PropertyModel;


@PropertyModel(prefix = "jboot.rpc.dubbo")
public class JbootDubborpcConfig {


    private String protocolName = "dubbo"; //default is dubbo
    private String protocolServer = "netty"; //default is netty
    private String protocolContextPath;
    private String protocolTransporter;
    private Integer protocolThreads;

    private Boolean qosEnable = false;
    private Integer qosPort;
    private Boolean qosAcceptForeignIp;


    private String protocolHost;

    // service port
    private Integer protocolPort;

    // context path
    private String protocolContextpath;

    // thread pool
    private String protocolThreadpool;

    // thread pool size (fixed size)

    // IO thread pool size (fixed size)
    private Integer protocolIothreads;

    // thread pool's queue length
    private Integer protocolQueues;

    // max acceptable connections
    private Integer protocolAccepts;

    // protocol codec
    private String protocolCodec;

    // serialization
    private String protocolSerialization;

    // charset
    private String protocolCharset;

    // payload max length
    private Integer protocolPayload;

    // buffer size
    private Integer protocolBuffer;

    // heartbeat interval
    private Integer protocolHeartbeat;

    // access log
    private String protocolAccesslog;


    // how information is exchanged
    private String protocolExchanger;

    // thread dispatch mode
    private String protocolDispatcher;

    // networker
    private String protocolNetworker;

    // client impl
    private String protocolClient;

    // supported telnet commands, separated with comma.
    private String protocolTelnet;

    // command line prompt
    private String protocolPrompt;

    // status check
    private String protocolStatus;

    // whether to register
    private Boolean protocolRegister;

    // parameters
    // 是否长连接
    // TODO add this to provider config
    private Boolean protocolKeepAlive;

    // TODO add this to provider config
    private String protocolOptimizer;

    private String protocolExtension;

    // if it's default
    private Boolean protocolIsDefault;


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

    public String getProtocolHost() {
        return protocolHost;
    }

    public void setProtocolHost(String protocolHost) {
        this.protocolHost = protocolHost;
    }

    public Integer getProtocolPort() {
        return protocolPort;
    }

    public void setProtocolPort(Integer protocolPort) {
        this.protocolPort = protocolPort;
    }

    public String getProtocolContextpath() {
        return protocolContextpath;
    }

    public void setProtocolContextpath(String protocolContextpath) {
        this.protocolContextpath = protocolContextpath;
    }

    public String getProtocolThreadpool() {
        return protocolThreadpool;
    }

    public void setProtocolThreadpool(String protocolThreadpool) {
        this.protocolThreadpool = protocolThreadpool;
    }

    public Integer getProtocolIothreads() {
        return protocolIothreads;
    }

    public void setProtocolIothreads(Integer protocolIothreads) {
        this.protocolIothreads = protocolIothreads;
    }

    public Integer getProtocolQueues() {
        return protocolQueues;
    }

    public void setProtocolQueues(Integer protocolQueues) {
        this.protocolQueues = protocolQueues;
    }

    public Integer getProtocolAccepts() {
        return protocolAccepts;
    }

    public void setProtocolAccepts(Integer protocolAccepts) {
        this.protocolAccepts = protocolAccepts;
    }

    public String getProtocolCodec() {
        return protocolCodec;
    }

    public void setProtocolCodec(String protocolCodec) {
        this.protocolCodec = protocolCodec;
    }

    public String getProtocolSerialization() {
        return protocolSerialization;
    }

    public void setProtocolSerialization(String protocolSerialization) {
        this.protocolSerialization = protocolSerialization;
    }

    public String getProtocolCharset() {
        return protocolCharset;
    }

    public void setProtocolCharset(String protocolCharset) {
        this.protocolCharset = protocolCharset;
    }

    public Integer getProtocolPayload() {
        return protocolPayload;
    }

    public void setProtocolPayload(Integer protocolPayload) {
        this.protocolPayload = protocolPayload;
    }

    public Integer getProtocolBuffer() {
        return protocolBuffer;
    }

    public void setProtocolBuffer(Integer protocolBuffer) {
        this.protocolBuffer = protocolBuffer;
    }

    public Integer getProtocolHeartbeat() {
        return protocolHeartbeat;
    }

    public void setProtocolHeartbeat(Integer protocolHeartbeat) {
        this.protocolHeartbeat = protocolHeartbeat;
    }

    public String getProtocolAccesslog() {
        return protocolAccesslog;
    }

    public void setProtocolAccesslog(String protocolAccesslog) {
        this.protocolAccesslog = protocolAccesslog;
    }

    public String getProtocolExchanger() {
        return protocolExchanger;
    }

    public void setProtocolExchanger(String protocolExchanger) {
        this.protocolExchanger = protocolExchanger;
    }

    public String getProtocolDispatcher() {
        return protocolDispatcher;
    }

    public void setProtocolDispatcher(String protocolDispatcher) {
        this.protocolDispatcher = protocolDispatcher;
    }

    public String getProtocolNetworker() {
        return protocolNetworker;
    }

    public void setProtocolNetworker(String protocolNetworker) {
        this.protocolNetworker = protocolNetworker;
    }

    public String getProtocolClient() {
        return protocolClient;
    }

    public void setProtocolClient(String protocolClient) {
        this.protocolClient = protocolClient;
    }

    public String getProtocolTelnet() {
        return protocolTelnet;
    }

    public void setProtocolTelnet(String protocolTelnet) {
        this.protocolTelnet = protocolTelnet;
    }

    public String getProtocolPrompt() {
        return protocolPrompt;
    }

    public void setProtocolPrompt(String protocolPrompt) {
        this.protocolPrompt = protocolPrompt;
    }

    public String getProtocolStatus() {
        return protocolStatus;
    }

    public void setProtocolStatus(String protocolStatus) {
        this.protocolStatus = protocolStatus;
    }

    public Boolean getProtocolRegister() {
        return protocolRegister;
    }

    public void setProtocolRegister(Boolean protocolRegister) {
        this.protocolRegister = protocolRegister;
    }

    public Boolean getProtocolKeepAlive() {
        return protocolKeepAlive;
    }

    public void setProtocolKeepAlive(Boolean protocolKeepAlive) {
        this.protocolKeepAlive = protocolKeepAlive;
    }

    public String getProtocolOptimizer() {
        return protocolOptimizer;
    }

    public void setProtocolOptimizer(String protocolOptimizer) {
        this.protocolOptimizer = protocolOptimizer;
    }

    public String getProtocolExtension() {
        return protocolExtension;
    }

    public void setProtocolExtension(String protocolExtension) {
        this.protocolExtension = protocolExtension;
    }

    public Boolean getProtocolIsDefault() {
        return protocolIsDefault;
    }

    public void setProtocolIsDefault(Boolean protocolIsDefault) {
        this.protocolIsDefault = protocolIsDefault;
    }

    public ProtocolConfig newProtocolConfig() {

        ProtocolConfig config = new ProtocolConfig();

        if (this.protocolDispatcher != null) {
            config.setDispatcher(this.protocolDispatcher);
        }
        if (this.protocolIsDefault != null) {
            config.setDefault(this.protocolIsDefault);
        }
        if (this.protocolClient != null) {
            config.setClient(this.protocolClient);
        }
        if (this.protocolCharset != null) {
            config.setCharset(this.protocolCharset);
        }
        if (this.protocolAccepts != null) {
            config.setAccepts(this.protocolAccepts);
        }
        if (this.protocolAccesslog != null) {
            config.setAccesslog(this.protocolAccesslog);
        }
        if (this.protocolBuffer != null) {
            config.setBuffer(this.protocolBuffer);
        }
        if (this.protocolCodec != null) {
            config.setCodec(this.protocolCodec);
        }
        if (this.protocolContextpath != null) {
            config.setContextpath(this.protocolContextpath);
        }
        if (this.protocolExchanger != null) {
            config.setExchanger(this.protocolExchanger);
        }
        if (this.protocolExtension != null) {
            config.setExtension(this.protocolExtension);
        }
        if (this.protocolHeartbeat != null) {
            config.setHeartbeat(this.protocolHeartbeat);
        }
        if (this.protocolHost != null) {
            config.setHost(this.protocolHost);
        }
        if (this.protocolIothreads != null) {
            config.setIothreads(this.protocolIothreads);
        }
        if (this.protocolKeepAlive != null) {
            config.setKeepAlive(this.protocolKeepAlive);
        }
        if (this.protocolName != null) {
            config.setName(this.protocolName);
        }
        if (this.protocolNetworker != null) {
            config.setNetworker(this.protocolNetworker);
        }
        if (this.protocolOptimizer != null) {
            config.setOptimizer(this.protocolOptimizer);
        }
        if (this.protocolPayload != null) {
            config.setPayload(this.protocolPayload);
        }
        if (this.protocolPort != null) {
            config.setPort(this.protocolPort);
        }
        if (this.protocolPrompt != null) {
            config.setPrompt(this.protocolPrompt);
        }
        if (this.protocolQueues != null) {
            config.setQueues(this.protocolQueues);
        }
        if (this.protocolRegister != null) {
            config.setRegister(this.protocolRegister);
        }
        if (this.protocolSerialization != null) {
            config.setSerialization(this.protocolSerialization);
        }
        if (this.protocolServer != null) {
            config.setServer(this.protocolServer);
        }
        if (this.protocolStatus != null) {
            config.setStatus(this.protocolStatus);
        }
        if (this.protocolTelnet != null) {
            config.setTelnet(this.protocolTelnet);
        }
        if (this.protocolThreadpool != null) {
            config.setThreadpool(this.protocolThreadpool);
        }
        if (this.protocolThreads != null) {
            config.setThreads(this.protocolThreads);
        }

        return config;
    }
}
