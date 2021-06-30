package io.jboot.apidoc;

import java.util.LinkedList;
import java.util.List;

public class ApiDocument {

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

}
