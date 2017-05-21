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
package io.jboot.rpc;

import io.jboot.config.annotation.PropertieConfig;


@PropertieConfig(prefix = "jboot.rpc")
public class JbootrpcConfig {

    public static final String TYPE_GRPC = "grpc";
    public static final String TYPE_MOTAN = "motan";
    public static final String TYPE_THRIFT = "thrift";
    public static final String TYPE_LOCAL = "local";

    public static final String REGISTRY_TYPE_CONSUL = "consul";
    public static final String REGISTRY_TYPE_ZOOKEEPER = "zookeeper";

    private String type = TYPE_LOCAL;
    private String requestTimeOut = "500";

    private String registryType = REGISTRY_TYPE_CONSUL;
    private String registryAddress = "127.0.0.1:8500";
    private String registryName = "jboot";

    private String defaultPort = "8088";
    private String defaultGroup = "jboot";
    private String defaultVersion = "1.0";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequestTimeOut() {
        return requestTimeOut;
    }

    public int getRequestTimeOutAsInt() {
        return Integer.valueOf(requestTimeOut);
    }

    public void setRequestTimeOut(String requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
    }

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getRegistryName() {
        return registryName;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public String getDefaultPort() {
        return defaultPort;
    }

    public void setDefaultPort(String defaultPort) {
        this.defaultPort = defaultPort;
    }

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public String getDefaultVersion() {
        return defaultVersion;
    }

    public void setDefaultVersion(String defaultVersion) {
        this.defaultVersion = defaultVersion;
    }
}
