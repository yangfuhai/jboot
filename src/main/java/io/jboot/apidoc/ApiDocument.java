/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.apidoc;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ApiDocument implements Serializable {

    private String value;
    private String notes;
    private String filePath;

    private List<ApiOperation> apiOperations;

    private Class<?> controllerClass;

    public ApiDocument() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFilePathByControllerPath(String controllerPath) {
        if ("/".equals(controllerPath)) {
            controllerPath = "index";
        } else if (controllerPath.startsWith("/")) {
            controllerPath = controllerPath.substring(1);
        }
        if (controllerPath.contains("/")) {
            controllerPath = controllerPath.replace("/", "_");
        }

        this.filePath = controllerPath;
    }

    public List<ApiOperation> getApiOperations() {
        return apiOperations;
    }

    public void setApiOperations(List<ApiOperation> apiOperations) {
        this.apiOperations = apiOperations;
    }

    public void addOperation(ApiOperation apiOperation) {
        if (apiOperations == null) {
            apiOperations = new LinkedList<>();
        }
        apiOperations.add(apiOperation);
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public void setControllerClass(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }

    @Override
    public String toString() {
        return "ApiDocument{" +
                "value='" + value + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
