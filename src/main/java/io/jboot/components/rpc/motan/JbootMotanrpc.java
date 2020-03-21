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
import io.jboot.components.rpc.JbootrpcBase;
import io.jboot.components.rpc.JbootrpcReferenceConfig;
import io.jboot.components.rpc.JbootrpcServiceConfig;
import io.jboot.utils.StrUtil;


public class JbootMotanrpc extends JbootrpcBase {

    @Override
    public void onInit() {
        MotanUtil.initMotan();
    }

    @Override
    public <T> T onServiceCreate(Class<T> serviceClass, JbootrpcReferenceConfig config) {
        RefererConfig<T> referer = MotanUtil.toRefererConfig(config);
        String directUrl = rpcConfig.getUrl(serviceClass.getName());
        if (StrUtil.isNotBlank(directUrl)){
            referer.setDirectUrl(directUrl);
        }
        return referer.getRef();
    }


    @Override
    public <T> boolean serviceExport(Class<T> interfaceClass, Object object, JbootrpcServiceConfig config) {

        synchronized (this) {

            MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, false);

            ServiceConfig<T> motanServiceConfig = MotanUtil.toServiceConfig(config);
            motanServiceConfig.setInterface(interfaceClass);
            motanServiceConfig.setRef((T) object);
            motanServiceConfig.export();

            MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
        }

        return true;
    }




}
