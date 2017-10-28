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
    private boolean serverEnable = false;

    /**
     * 给远程提供的配置文件的路径，多个用分号（；）隔开
     */
    private String path;

    /**
     * 要排除的配置文件
     */
    private String exclude;


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

    public boolean isServerEnable() {
        return serverEnable;
    }

    public void setServerEnable(boolean serverEnable) {
        this.serverEnable = serverEnable;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExclude() {
        return exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }
}
