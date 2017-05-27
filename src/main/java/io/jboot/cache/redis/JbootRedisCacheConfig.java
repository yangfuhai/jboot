/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.cache.redis;


import io.jboot.config.annotation.PropertieConfig;

/**
 * JbootRedis 缓存的配置文件
 */
@PropertieConfig(prefix = "jboot.cache.redis")
public class JbootRedisCacheConfig {


    private String address;
    private String password;
    private String database;

    private String timeout = "3000";
    private String pingTimeout = "1000";
    private String connectTimeout = "10000";
    private String reconnectTimeout = "3000";
    private String idleConnectionTimeout = "10000";

    private String retryAttempts = "3";
    private String failedAttempts = "3";
    private String retryInterval = "1500";


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getPingTimeout() {
        return pingTimeout;
    }

    public void setPingTimeout(String pingTimeout) {
        this.pingTimeout = pingTimeout;
    }

    public String getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(String connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getReconnectTimeout() {
        return reconnectTimeout;
    }

    public void setReconnectTimeout(String reconnectTimeout) {
        this.reconnectTimeout = reconnectTimeout;
    }

    public String getIdleConnectionTimeout() {
        return idleConnectionTimeout;
    }

    public void setIdleConnectionTimeout(String idleConnectionTimeout) {
        this.idleConnectionTimeout = idleConnectionTimeout;
    }

    public String getRetryAttempts() {
        return retryAttempts;
    }

    public void setRetryAttempts(String retryAttempts) {
        this.retryAttempts = retryAttempts;
    }

    public String getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(String failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public String getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(String retryInterval) {
        this.retryInterval = retryInterval;
    }

    public boolean isCluster() {
        return address != null && address.indexOf(",") != -1;
    }
}
