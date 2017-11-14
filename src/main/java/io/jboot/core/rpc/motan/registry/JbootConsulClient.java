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
import com.ecwid.consul.v1.ConsulRawClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.AgentClient;
import com.ecwid.consul.v1.agent.model.*;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Description: 主要是 替换 agentClient 为 JbootAgentConsulClient
 * @Package io.jboot.core.rpc.motan.registry
 */
public class JbootConsulClient extends ConsulClient {


    private final AgentClient agentClient;

    public JbootConsulClient(ConsulRawClient rawClient) {
        super(rawClient);
        agentClient = new JbootAgentConsulClient(rawClient);
    }


    /**
     * Connect to consul agent on specific address and default port (8500)
     *
     * @param agentHost Hostname or IP address of consul agent. You can specify scheme
     *                  (HTTP/HTTPS) in address. If there is no scheme in address -
     *                  client will use HTTP.
     */
    public JbootConsulClient(String agentHost) {
        this(new ConsulRawClient(agentHost));
    }


    // -------------------------------------------------------------------------------------------
    // Agent

    @Override
    public Response<Map<String, Check>> getAgentChecks() {
        return agentClient.getAgentChecks();
    }

    @Override
    public Response<Map<String, Service>> getAgentServices() {
        return agentClient.getAgentServices();
    }

    @Override
    public Response<List<Member>> getAgentMembers() {
        return agentClient.getAgentMembers();
    }

    @Override
    public Response<Self> getAgentSelf() {
        return agentClient.getAgentSelf();
    }

    @Override
    public Response<Self> getAgentSelf(String token) {
        return agentClient.getAgentSelf(token);
    }

    @Override
    public Response<Void> agentSetMaintenance(boolean maintenanceEnabled) {
        return agentClient.agentSetMaintenance(maintenanceEnabled);
    }

    @Override
    public Response<Void> agentSetMaintenance(boolean maintenanceEnabled, String reason) {
        return agentClient.agentSetMaintenance(maintenanceEnabled, reason);
    }

    @Override
    public Response<Void> agentJoin(String address, boolean wan) {
        return agentClient.agentJoin(address, wan);
    }

    @Override
    public Response<Void> agentForceLeave(String node) {
        return agentClient.agentForceLeave(node);
    }

    @Override
    public Response<Void> agentCheckRegister(NewCheck newCheck) {
        return agentClient.agentCheckRegister(newCheck);
    }

    @Override
    public Response<Void> agentCheckRegister(NewCheck newCheck, String token) {
        return agentClient.agentCheckRegister(newCheck, token);
    }

    @Override
    public Response<Void> agentCheckDeregister(String checkId) {
        return agentClient.agentCheckDeregister(checkId);
    }

    @Override
    public Response<Void> agentCheckPass(String checkId) {
        return agentClient.agentCheckPass(checkId);
    }

    @Override
    public Response<Void> agentCheckPass(String checkId, String note) {
        return agentClient.agentCheckPass(checkId, note);
    }

    @Override
    public Response<Void> agentCheckWarn(String checkId) {
        return agentClient.agentCheckWarn(checkId);
    }

    @Override
    public Response<Void> agentCheckWarn(String checkId, String note) {
        return agentClient.agentCheckWarn(checkId, note);
    }

    @Override
    public Response<Void> agentCheckFail(String checkId) {
        return agentClient.agentCheckFail(checkId);
    }

    @Override
    public Response<Void> agentCheckFail(String checkId, String note) {
        return agentClient.agentCheckFail(checkId, note);
    }

    @Override
    public Response<Void> agentServiceRegister(NewService newService) {
        return agentClient.agentServiceRegister(newService);
    }

    @Override
    public Response<Void> agentServiceRegister(NewService newService, String token) {
        return agentClient.agentServiceRegister(newService, token);
    }

    @Override
    public Response<Void> agentServiceDeregister(String serviceId) {
        return agentClient.agentServiceDeregister(serviceId);
    }

    @Override
    public Response<Void> agentServiceDeregister(String serviceId, String token) {
        return agentClient.agentServiceDeregister(serviceId, token);
    }

    @Override
    public Response<Void> agentServiceSetMaintenance(String serviceId, boolean maintenanceEnabled) {
        return agentClient.agentServiceSetMaintenance(serviceId, maintenanceEnabled);
    }

    @Override
    public Response<Void> agentServiceSetMaintenance(String serviceId, boolean maintenanceEnabled, String reason) {
        return agentClient.agentServiceSetMaintenance(serviceId, maintenanceEnabled, reason);
    }

    @Override
    public Response<Void> agentReload() {
        return agentClient.agentReload();
    }

}
