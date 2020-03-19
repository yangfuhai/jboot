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
public class ConsumerConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Whether to use the default protocol
     */
    private Boolean isDefault;

    /**
     * Networking framework client uses: netty, mina, etc.
     */
    private String client;

    /**
     * Consumer thread pool type: cached, fixed, limit, eager
     */
    private String threadpool;

    /**
     * Consumer threadpool core thread size
     */
    private Integer corethreads;

    /**
     * Consumer threadpool thread size
     */
    private Integer threads;

    /**
     * Consumer threadpool queue size
     */
    private Integer queues;

    /**
     * By default, a TCP long-connection communication is shared between the consumer process and the provider process.
     * This property can be set to share multiple TCP long-connection communications. Note that only the dubbo protocol takes effect.
     */
    private Integer shareconnections;

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
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

    public Integer getQueues() {
        return queues;
    }

    public void setQueues(Integer queues) {
        this.queues = queues;
    }

    public Integer getShareconnections() {
        return shareconnections;
    }

    public void setShareconnections(Integer shareconnections) {
        this.shareconnections = shareconnections;
    }
}
