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

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/19
 */
public class ProviderConfig  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Service ip addresses (used when there are multiple network cards available)
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
     * Thread pool size (fixed size)
     */
    private Integer threads;

    /**
     * IO thread pool size (fixed size)
     */
    private Integer iothreads;

    /**
     * Thread pool queue length
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
     * The serialization charset
     */
    private String charset;

    /**
     * Payload max length
     */
    private Integer payload;

    /**
     * The network io buffer size
     */
    private Integer buffer;

    /**
     * Transporter
     */
    private String transporter;

    /**
     * How information gets exchanged
     */
    private String exchanger;

    /**
     * Thread dispatching mode
     */
    private String dispatcher;

    /**
     * Networker
     */
    private String networker;

    /**
     * The server-side implementation model of the protocol
     */
    private String server;

    /**
     * The client-side implementation model of the protocol
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
     * Wait time when stop
     */
    private Integer wait;

    /**
     * Whether to use the default protocol
     */
    private Boolean isDefault;

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

    public Integer getWait() {
        return wait;
    }

    public void setWait(Integer wait) {
        this.wait = wait;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
