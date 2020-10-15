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

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.RefererConfig;
import com.weibo.api.motan.config.ServiceConfig;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import io.jboot.Jboot;
import io.jboot.components.rpc.JbootrpcBase;
import io.jboot.components.rpc.JbootrpcReferenceConfig;
import io.jboot.components.rpc.JbootrpcServiceConfig;
import io.jboot.utils.StrUtil;


public class JbootMotanrpc extends JbootrpcBase {

    private MotanrpcConfig defaultConfig = Jboot.config(MotanrpcConfig.class);

    @Override
    public void onStart() {
        MotanUtil.initMotan();
    }

    @Override
    public <T> T onServiceCreate(Class<T> interfaceClass, JbootrpcReferenceConfig config) {
        RefererConfig<T> referer = MotanUtil.toRefererConfig(config);
        referer.setInterface(interfaceClass);

        String directUrl = rpcConfig.getUrl(interfaceClass.getName());
        if (StrUtil.isNotBlank(directUrl)) {
            referer.setDirectUrl(directUrl);
        }

        String consumer = rpcConfig.getConsumer(interfaceClass.getName());
        if (consumer != null) {
            referer.setBasicReferer(MotanUtil.getBaseReferer(consumer));
        }

        if (referer.getGroup() == null) {
            referer.setGroup(rpcConfig.getGroup(interfaceClass.getName()));
        }

        if (referer.getVersion() == null) {
            referer.setVersion(rpcConfig.getVersion(interfaceClass.getName()));
        }

        return referer.getRef();
    }


    @Override
    public <T> boolean serviceExport(Class<T> interfaceClass, Object object, JbootrpcServiceConfig config) {

        synchronized (this) {

            MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, false);

            ServiceConfig<T> service = MotanUtil.toServiceConfig(config);
            service.setInterface(interfaceClass);
            service.setRef((T) object);
            service.setShareChannel(true);
            service.setExport(defaultConfig.getExport(interfaceClass.getName()));
            service.setHost(defaultConfig.getHost(interfaceClass.getName()));

            String provider = rpcConfig.getProvider(interfaceClass.getName());
            if (provider != null) {
                service.setBasicService(MotanUtil.getBaseService(provider));
            }

            if (service.getGroup() == null) {
                service.setGroup(rpcConfig.getGroup(interfaceClass.getName()));
            }

            if (service.getVersion() == null) {
                service.setVersion(rpcConfig.getVersion(interfaceClass.getName()));
            }

            service.export();
            MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
        }

        return true;
    }


}
