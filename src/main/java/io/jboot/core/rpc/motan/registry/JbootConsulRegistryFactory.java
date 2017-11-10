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
package io.jboot.core.rpc.motan.registry;

import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.registry.Registry;
import com.weibo.api.motan.registry.consul.ConsulConstants;
import com.weibo.api.motan.registry.consul.ConsulRegistry;
import com.weibo.api.motan.registry.consul.client.MotanConsulClient;
import com.weibo.api.motan.registry.support.AbstractRegistryFactory;
import com.weibo.api.motan.rpc.URL;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Description: 用于修复 motan consul1.0 无法注册的问题 ，问题出现在 consul.10对 对http get 和 put 进行了验证。
 * 而AgentConsulClient.java 把很多get请求和put请求搞混淆了，因此需要对AgentConsulClient 进行重写。
 * 具体的get或put请求请参考：https://github.com/hashicorp/consul/issues/3659
 * @Package io.jboot.core.rpc.motan.registry
 */
@SpiMeta(name = "jbootconsul")
public class JbootConsulRegistryFactory extends AbstractRegistryFactory {
    @Override
    protected Registry createRegistry(URL url) {
        String host = ConsulConstants.DEFAULT_HOST;
        int port = ConsulConstants.DEFAULT_PORT;
        if (StringUtils.isNotBlank(url.getHost())) {
            host = url.getHost();
        }
        if (url.getPort() > 0) {
            port = url.getPort();
        }
        //可以使用不同的client实现
        MotanConsulClient client = new JbootConsulEcwidClient(host, port);
        return new ConsulRegistry(url, client);
    }
}
