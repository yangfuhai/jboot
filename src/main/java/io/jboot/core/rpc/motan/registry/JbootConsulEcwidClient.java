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

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.health.model.HealthService;
import com.ecwid.consul.v1.health.model.HealthService.Service;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.weibo.api.motan.registry.consul.ConsulConstants;
import com.weibo.api.motan.registry.consul.ConsulResponse;
import com.weibo.api.motan.registry.consul.ConsulService;
import com.weibo.api.motan.registry.consul.ConsulUtils;
import com.weibo.api.motan.registry.consul.client.MotanConsulClient;
import com.weibo.api.motan.util.LoggerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Description: 主要是 替换 ConsulClient 为 JbootConsulClient
 * @Package io.jboot.core.rpc.motan.registry
 */
public class JbootConsulEcwidClient extends MotanConsulClient {
    public static ConsulClient client;

    public JbootConsulEcwidClient(String host, int port) {
        super(host, port);
        client = new ConsulClient(host, port);
        LoggerUtil.info("JbootConsulEcwidClient init finish. client host:" + host
                + ", port:" + port);
    }

    @Override
    public void checkPass(String serviceid) {
        client.agentCheckPass("service:" + serviceid);
    }

    @Override
    public void registerService(ConsulService service) {
        NewService newService = convertService(service);
        client.agentServiceRegister(newService);
    }

    @Override
    public void unregisterService(String serviceid) {
        client.agentServiceDeregister(serviceid);
    }

    @Override
    public ConsulResponse<List<ConsulService>> lookupHealthService(
            String serviceName, long lastConsulIndex) {
        QueryParams queryParams = new QueryParams(
                ConsulConstants.CONSUL_BLOCK_TIME_SECONDS, lastConsulIndex);
        Response<List<HealthService>> orgResponse = client.getHealthServices(
                serviceName, true, queryParams);
        ConsulResponse<List<ConsulService>> newResponse = null;
        if (orgResponse != null && orgResponse.getValue() != null
                && !orgResponse.getValue().isEmpty()) {
            List<HealthService> HealthServices = orgResponse.getValue();
            List<ConsulService> ConsulServices = new ArrayList<ConsulService>(
                    HealthServices.size());

            for (HealthService orgService : HealthServices) {
                try {
                    ConsulService newService = convertToConsulService(orgService);
                    ConsulServices.add(newService);
                } catch (Exception e) {
                    String servcieid = "null";
                    if (orgService.getService() != null) {
                        servcieid = orgService.getService().getId();
                    }
                    LoggerUtil.error(
                            "convert consul service fail. org consulservice:"
                                    + servcieid, e);
                }
            }
            if (!ConsulServices.isEmpty()) {
                newResponse = new ConsulResponse<List<ConsulService>>();
                newResponse.setValue(ConsulServices);
                newResponse.setConsulIndex(orgResponse.getConsulIndex());
                newResponse.setConsulLastContact(orgResponse
                        .getConsulLastContact());
                newResponse.setConsulKnownLeader(orgResponse
                        .isConsulKnownLeader());
            }
        }

        return newResponse;
    }

    @Override
    public String lookupCommand(String group) {
        Response<GetValue> response = client.getKVValue(ConsulConstants.CONSUL_MOTAN_COMMAND + ConsulUtils.convertGroupToServiceName(group));
        GetValue value = response.getValue();
        String command = "";
        if (value == null) {
            LoggerUtil.info("no command in group: " + group);
        } else if (value.getValue() != null) {
            command = value.getDecodedValue();
        }
        return command;
    }

    private NewService convertService(ConsulService service) {
        NewService newService = new NewService();
        newService.setAddress(service.getAddress());
        newService.setId(service.getId());
        newService.setName(service.getName());
        newService.setPort(service.getPort());
        newService.setTags(service.getTags());
        NewService.Check check = new NewService.Check();
        check.setTtl(service.getTtl() + "s");
        newService.setCheck(check);
        return newService;
    }

    private ConsulService convertToConsulService(HealthService healthService) {
        ConsulService service = new ConsulService();
        Service org = healthService.getService();
        service.setAddress(org.getAddress());
        service.setId(org.getId());
        service.setName(org.getService());
        service.setPort(org.getPort());
        service.setTags(org.getTags());
        return service;
    }

    @Override
    public void checkFail(String serviceid) {
        client.agentCheckFail("service:" + serviceid);
    }

}
