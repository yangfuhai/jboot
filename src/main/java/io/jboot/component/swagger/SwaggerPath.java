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
package io.jboot.component.swagger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.component.swagger
 */
public class SwaggerPath {

    private String method;
    private String path;
    private String tags;
    private String summary;
    private String description;
    private String operationId;
    private List<Map> parameters;
    private Map responses;
    private String contentType;


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map getResponses() {
        return responses;
    }

    public void setResponses(Map responses) {
        this.responses = responses;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public List<Map> getParameters() {
        return parameters;
    }

    public void setParameters(List<Map> parameters) {
        this.parameters = parameters;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void addResponse(String key, Map value) {
        if (responses == null) {
            responses = Maps.newHashMap();
        }
        responses.put(key, value);
    }

    public Map toMap() {

        Map rootMap = Maps.newHashMap();

        Map infoMap = Maps.newHashMap();
        infoMap.put("tags", Lists.newArrayList(this.tags));
        infoMap.put("summary", this.summary);
        infoMap.put("description", this.description);
        infoMap.put("operationId", this.operationId);
        infoMap.put("consumes", Lists.newArrayList(this.contentType));
        infoMap.put("parameters", this.parameters);
        infoMap.put("responses", this.responses);

        rootMap.put(method, infoMap);
        return rootMap;
    }
}
