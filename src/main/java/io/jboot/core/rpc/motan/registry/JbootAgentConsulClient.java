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

import com.ecwid.consul.SingleUrlParameters;
import com.ecwid.consul.UrlParameters;
import com.ecwid.consul.json.GsonFactory;
import com.ecwid.consul.transport.RawResponse;
import com.ecwid.consul.v1.ConsulRawClient;
import com.ecwid.consul.v1.OperationException;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.AgentClient;
import com.ecwid.consul.v1.agent.model.*;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Description: 主要 是修复了 consul 1.0 升级之后，对http get 和 put 进行验证的问题
 * AgentConsulClient.java 把很多get请求和put请求搞混淆了，具体的get或put请求请参考：https://github.com/hashicorp/consul/issues/3659
 * @Package io.jboot.core.rpc.motan.registry
 */
public class JbootAgentConsulClient implements AgentClient {
    private final ConsulRawClient rawClient;

    public JbootAgentConsulClient(ConsulRawClient rawClient) {
        this.rawClient = rawClient;
    }
    

    @Override
    public Response<Map<String, Check>> getAgentChecks() {
        RawResponse rawResponse = rawClient.makeGetRequest("/v1/agent/checks");

        if (rawResponse.getStatusCode() == 200) {
            Map<String, Check> value = GsonFactory.getGson().fromJson(rawResponse.getContent(), new TypeToken<Map<String, Check>>() {
            }.getType());
            return new Response<Map<String, Check>>(value, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }
    }

    @Override
    public Response<Map<String, Service>> getAgentServices() {
        RawResponse rawResponse = rawClient.makeGetRequest("/v1/agent/services");

        if (rawResponse.getStatusCode() == 200) {
            Map<String, Service> agentServices = GsonFactory.getGson().fromJson(rawResponse.getContent(),
                    new TypeToken<Map<String, Service>>() {
                    }.getType());
            return new Response<Map<String, Service>>(agentServices, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }
    }

    @Override
    public Response<List<Member>> getAgentMembers() {
        RawResponse rawResponse = rawClient.makeGetRequest("/v1/agent/members");

        if (rawResponse.getStatusCode() == 200) {
            List<Member> members = GsonFactory.getGson().fromJson(rawResponse.getContent(), new TypeToken<List<Member>>() {
            }.getType());
            return new Response<List<Member>>(members, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }
    }

    @Override
    public Response<Self> getAgentSelf() {
        return getAgentSelf(null);
    }

    @Override
    public Response<Self> getAgentSelf(String token) {
        UrlParameters tokenParam = token != null ? new SingleUrlParameters("token", token) : null;

        RawResponse rawResponse = rawClient.makeGetRequest("/v1/agent/self", tokenParam);

        if (rawResponse.getStatusCode() == 200) {
            Self self = GsonFactory.getGson().fromJson(rawResponse.getContent(), Self.class);
            return new Response<Self>(self, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }
    }

    @Override
    public Response<Void> agentSetMaintenance(boolean maintenanceEnabled) {
        return agentSetMaintenance(maintenanceEnabled, null);
    }

    @Override
    public Response<Void> agentSetMaintenance(boolean maintenanceEnabled, String reason) {
        UrlParameters maintenanceParameter = new SingleUrlParameters("enable", Boolean.toString(maintenanceEnabled));
        UrlParameters reasonParamenter = reason != null ? new SingleUrlParameters("reason", reason) : null;

        RawResponse rawResponse = rawClient.makePutRequest("/v1/agent/maintenance", "", maintenanceParameter, reasonParamenter);

        if (rawResponse.getStatusCode() == 200) {
            return new Response<Void>(null, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }

    }

    @Override
    public Response<Void> agentJoin(String address, boolean wan) {
        UrlParameters wanParams = wan ? new SingleUrlParameters("wan", "1") : null;
        RawResponse rawResponse = rawClient.makePutRequest("/v1/agent/join/" + address, "", wanParams);

        if (rawResponse.getStatusCode() == 200) {
            return new Response<Void>(null, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }
    }

    @Override
    public Response<Void> agentForceLeave(String node) {
        RawResponse rawResponse = rawClient.makePutRequest("/v1/agent/force-leave/" + node, "");

        if (rawResponse.getStatusCode() == 200) {
            return new Response<Void>(null, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }
    }

    @Override
    public Response<Void> agentCheckRegister(NewCheck newCheck) {
        return agentCheckRegister(newCheck, null);
    }

    @Override
    public Response<Void> agentCheckRegister(NewCheck newCheck, String token) {
        UrlParameters tokenParam = token != null ? new SingleUrlParameters("token", token) : null;

        String json = GsonFactory.getGson().toJson(newCheck);
        RawResponse rawResponse = rawClient.makePutRequest("/v1/agent/check/register", json, tokenParam);

        if (rawResponse.getStatusCode() == 200) {
            return new Response<Void>(null, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }
    }

    @Override
    public Response<Void> agentCheckDeregister(String checkId) {
        RawResponse rawResponse = rawClient.makePutRequest("/v1/agent/check/deregister/" + checkId, "");

        if (rawResponse.getStatusCode() == 200) {
            return new Response<Void>(null, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }
    }

    @Override
    public Response<Void> agentCheckPass(String checkId) {
        return agentCheckPass(checkId, null);
    }

    @Override
    public Response<Void> agentCheckPass(String checkId, String note) {
        UrlParameters noteParams = note != null ? new SingleUrlParameters("note", note) : null;
        RawResponse rawResponse = rawClient.makePutRequest("/v1/agent/check/pass/" + checkId, "", noteParams);

        if (rawResponse.getStatusCode() == 200) {
            return new Response<Void>(null, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }
    }

    @Override
    public Response<Void> agentCheckWarn(String checkId) {
        return agentCheckWarn(checkId, null);
    }

    @Override
    public Response<Void> agentCheckWarn(String checkId, String note) {
        UrlParameters noteParams = note != null ? new SingleUrlParameters("note", note) : null;
        RawResponse rawResponse = rawClient.makePutRequest("/v1/agent/check/warn/" + checkId, "", noteParams);

        if (rawResponse.getStatusCode() == 200) {
            return new Response<Void>(null, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }
    }

    @Override
    public Response<Void> agentCheckFail(String checkId) {
        return agentCheckFail(checkId, null);
    }

    @Override
    public Response<Void> agentCheckFail(String checkId, String note) {
        UrlParameters noteParams = note != null ? new SingleUrlParameters("note", note) : null;
        RawResponse rawResponse = rawClient.makePutRequest("/v1/agent/check/fail/" + checkId, "", noteParams);

        if (rawResponse.getStatusCode() == 200) {
            return new Response<Void>(null, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }
    }

    @Override
    public Response<Void> agentServiceRegister(NewService newService) {
        return agentServiceRegister(newService, null);
    }

    @Override
    public Response<Void> agentServiceRegister(NewService newService, String token) {
        UrlParameters tokenParam = token != null ? new SingleUrlParameters("token", token) : null;

        String json = GsonFactory.getGson().toJson(newService);
        RawResponse rawResponse = rawClient.makePutRequest("/v1/agent/service/register", json, tokenParam);

        if (rawResponse.getStatusCode() == 200) {
            return new Response<Void>(null, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }
    }

    @Override
    public Response<Void> agentServiceDeregister(String serviceId) {
        return agentServiceDeregister(serviceId, null);
    }

    @Override
    public Response<Void> agentServiceDeregister(String serviceId, String token) {
        UrlParameters tokenParam = token != null ? new SingleUrlParameters("token", token) : null;

        RawResponse rawResponse = rawClient.makePutRequest("/v1/agent/service/deregister/" + serviceId, "", tokenParam);

        if (rawResponse.getStatusCode() == 200) {
            return new Response<Void>(null, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }
    }

    @Override
    public Response<Void> agentServiceSetMaintenance(String serviceId, boolean maintenanceEnabled) {
        return agentServiceSetMaintenance(serviceId, maintenanceEnabled, null);
    }

    @Override
    public Response<Void> agentServiceSetMaintenance(String serviceId, boolean maintenanceEnabled, String reason) {
        UrlParameters maintenanceParameter = new SingleUrlParameters("enable", Boolean.toString(maintenanceEnabled));
        UrlParameters reasonParameter = reason != null ? new SingleUrlParameters("reason", reason) : null;

        RawResponse rawResponse = rawClient.makePutRequest("/v1/agent/service/maintenance/" + serviceId, "", maintenanceParameter, reasonParameter);

        if (rawResponse.getStatusCode() == 200) {
            return new Response<Void>(null, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }
    }

    @Override
    public Response<Void> agentReload() {
        RawResponse rawResponse = rawClient.makePutRequest("/v1/agent/reload", "");

        if (rawResponse.getStatusCode() == 200) {
            return new Response<Void>(null, rawResponse);
        } else {
            throw new OperationException(rawResponse);
        }

    }
}
