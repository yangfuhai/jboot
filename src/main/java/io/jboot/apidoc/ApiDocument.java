package io.jboot.apidoc;

import java.util.LinkedList;
import java.util.List;

public class ApiDocument {

    private String value;
    private String notes;
    private String filePath;

    private List<ApiOperation> apiOperations;

    private String controllerPath;
    private Class<?> controllerClass;
    private HttpMethod controllerMethod;

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

    public String getControllerPath() {
        return controllerPath;
    }

    public void setControllerPath(String controllerPath) {
        this.controllerPath = controllerPath;
    }

    public void setMappingAndFilePath(String mapping) {
        this.controllerPath = mapping;

        if ("/".equals(mapping)) {
            mapping = "index";
        } else if (mapping.startsWith("/")) {
            mapping = mapping.substring(1);
        }
        if (mapping.contains("/")) {
            mapping = mapping.replace("/", "_");
        }

        this.filePath = mapping;
    }

    public List<ApiOperation> getApiOperations() {
        return apiOperations;
    }

    public void setApiOperations(List<ApiOperation> apiOperations) {
        this.apiOperations = apiOperations;
    }

    public void addOperation(ApiOperation apiOperation) {
        if (apiOperations == null){
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

    public HttpMethod getControllerMethod() {
        return controllerMethod;
    }

    public void setControllerMethod(HttpMethod controllerMethod) {
        this.controllerMethod = controllerMethod;
    }


}
