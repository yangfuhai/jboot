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
package io.jboot.components.rpc.motan;

import io.jboot.app.config.annotation.ConfigModel;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/31
 * @see com.weibo.api.motan.config.AbstractServiceConfig
 */
@ConfigModel(prefix = "jboot.rpc.motan")
public class MotanrpcConfig {

    /**
     * 一个service可以按多个protocol提供服务，不同protocol使用不同port 利用export来设置protocol和port，格式如下：
     * protocol1:port1,protocol2:port2
     **/
    protected String defaultExport;


    /**
     * 一般不用设置，由服务自己获取，但如果有多个ip，而只想用指定ip，则可以在此处指定
     */
    protected String defaultHost;


    public String getDefaultExport() {
        return defaultExport;
    }

    public void setDefaultExport(String defaultExport) {
        this.defaultExport = defaultExport;
    }

    public String getDefaultHost() {
        return defaultHost;
    }

    public void setDefaultHost(String defaultHost) {
        this.defaultHost = defaultHost;
    }
}
