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
package io.jboot.rpc.motan;

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.*;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import io.jboot.rpc.JbootrpcConfig;
import io.jboot.rpc.JbootrpcBase;


public class JbootMotanrpc extends JbootrpcBase {


    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;

    public JbootMotanrpc(JbootrpcConfig config) {
        super(config);
        initRegistryConfig();
        initProtocolConfig();
    }

    private void initProtocolConfig() {
        protocolConfig = new ProtocolConfig();
        protocolConfig.setId("motan");
        protocolConfig.setName("motan");
    }

    private void initRegistryConfig() {
        registryConfig = new RegistryConfig();
        registryConfig.setRegProtocol(getConfig().getRegistryType());
        registryConfig.setAddress(getConfig().getRegistryAddress());
        registryConfig.setName(getConfig().getRegistryName());
    }

    @Override
    public <T> T serviceObtain(Class<T> serviceClass, String group, String version) {
        RefererConfig<T> refererConfig = new RefererConfig<T>();

        // 设置接口及实现类
        refererConfig.setInterface(serviceClass);

        // 配置服务的group以及版本号
        refererConfig.setGroup(group);
        refererConfig.setVersion(version);
        refererConfig.setRequestTimeout(getConfig().getRequestTimeOutAsInt());
        initConfig(refererConfig);

        return refererConfig.getRef();
    }


    @Override
    public <T> boolean serviceExport(Class<T> interfaceClass, Object object, String group, String version, int port) {

        synchronized (this) {

            MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, false);

            ServiceConfig<T> motanServiceConfig = new ServiceConfig<T>();
            initConfig(motanServiceConfig);

            // 设置接口及实现类
            motanServiceConfig.setInterface(interfaceClass);
            motanServiceConfig.setRef((T) object);

            // 配置服务的group以及版本号
            motanServiceConfig.setGroup(group);
            motanServiceConfig.setVersion(version);

            motanServiceConfig.setShareChannel(true);
            motanServiceConfig.setExport(String.format("motan:%s", port));
            motanServiceConfig.export();

            MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
        }

        return true;
    }

    private void initConfig(AbstractInterfaceConfig config) {
        config.setRegistry(registryConfig);
        config.setProtocol(protocolConfig);
    }


}
