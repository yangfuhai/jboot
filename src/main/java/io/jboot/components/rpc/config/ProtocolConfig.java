/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.components.rpc.config;

import java.io.Serializable;
import java.util.Map;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/19
 * http://dubbo.apache.org/zh-cn/docs/user/references/xml/dubbo-protocol.html
 */
public class ProtocolConfig implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * Protocol name
     */
    private String name;

    /**
     * Service ip address (when there are multiple network cards available)
     */
    private String host;

    /**
     * Service port
     */
    private Integer port;

    /**
     * Context path
     */
    private String contextpath;

    /**
     * Thread pool
     */
    private String threadpool;

    /**
     * Thread pool core thread size
     */
    private Integer corethreads;

    /**
     * Thread pool size (fixed size)
     */
    private Integer threads;

    /**
     * IO thread pool size (fixed size)
     */
    private Integer iothreads;

    /**
     * Thread pool's queue length
     */
    private Integer queues;

    /**
     * Max acceptable connections
     */
    private Integer accepts;

    /**
     * Protocol codec
     */
    private String codec;

    /**
     * Serialization
     */
    private String serialization;

    /**
     * Charset
     */
    private String charset;

    /**
     * Payload max length
     */
    private Integer payload;

    /**
     * Buffer size
     */
    private Integer buffer;

    /**
     * Heartbeat interval
     */
    private Integer heartbeat;

    /**
     * Access log
     */
    private String accesslog;

    /**
     * Transfort
     */
    private String transporter;

    /**
     * How information is exchanged
     */
    private String exchanger;

    /**
     * Thread dispatch mode
     */
    private String dispatcher;

    /**
     * Networker
     */
    private String networker;

    /**
     * Sever impl
     */
    private String server;

    /**
     * Client impl
     */
    private String client;

    /**
     * Supported telnet commands, separated with comma.
     */
    private String telnet;

    /**
     * Command line prompt
     */
    private String prompt;

    /**
     * Status check
     */
    private String status;

    /**
     * Whether to register
     */
    private Boolean register;

    /**
     * whether it is a persistent connection
     */
    //TODO add this to provider config
    private Boolean keepAlive;

    // TODO add this to provider config
    private String optimizer;

    /**
     * The extension
     */
    private String extension;

    /**
     * The customized parameters
     */
    private Map<String, String> parameters;

    /**
     * If it's default
     */
    private Boolean isDefault;

    private Boolean sslEnabled;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getContextpath() {
        return contextpath;
    }

    public void setContextpath(String contextpath) {
        this.contextpath = contextpath;
    }

    public String getThreadpool() {
        return threadpool;
    }

    public void setThreadpool(String threadpool) {
        this.threadpool = threadpool;
    }

    public Integer getCorethreads() {
        return corethreads;
    }

    public void setCorethreads(Integer corethreads) {
        this.corethreads = corethreads;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Integer getIothreads() {
        return iothreads;
    }

    public void setIothreads(Integer iothreads) {
        this.iothreads = iothreads;
    }

    public Integer getQueues() {
        return queues;
    }

    public void setQueues(Integer queues) {
        this.queues = queues;
    }

    public Integer getAccepts() {
        return accepts;
    }

    public void setAccepts(Integer accepts) {
        this.accepts = accepts;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Integer getPayload() {
        return payload;
    }

    public void setPayload(Integer payload) {
        this.payload = payload;
    }

    public Integer getBuffer() {
        return buffer;
    }

    public void setBuffer(Integer buffer) {
        this.buffer = buffer;
    }

    public Integer getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(Integer heartbeat) {
        this.heartbeat = heartbeat;
    }

    public String getAccesslog() {
        return accesslog;
    }

    public void setAccesslog(String accesslog) {
        this.accesslog = accesslog;
    }

    public String getTransporter() {
        return transporter;
    }

    public void setTransporter(String transporter) {
        this.transporter = transporter;
    }

    public String getExchanger() {
        return exchanger;
    }

    public void setExchanger(String exchanger) {
        this.exchanger = exchanger;
    }

    public String getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(String dispatcher) {
        this.dispatcher = dispatcher;
    }

    public String getNetworker() {
        return networker;
    }

    public void setNetworker(String networker) {
        this.networker = networker;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getTelnet() {
        return telnet;
    }

    public void setTelnet(String telnet) {
        this.telnet = telnet;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    public Boolean getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getOptimizer() {
        return optimizer;
    }

    public void setOptimizer(String optimizer) {
        this.optimizer = optimizer;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Boolean getSslEnabled() {
        return sslEnabled;
    }

    public void setSslEnabled(Boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }
}
