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
package io.jboot.support.swagger;

import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>ReaderContext</code> class is wrapper for the <code>Reader</code> parameters.
 */
public class ReaderContext {

    private Swagger swagger;
    private Class<?> cls;
    private String parentPath;
    private String parentHttpMethod;
    private boolean readHidden;
    private List<String> parentConsumes = new ArrayList<>();
    private List<String> parentProduces = new ArrayList<>();
    private List<String> parentTags = new ArrayList<>();
    private List<Parameter> parentParameters = new ArrayList<>();

    public ReaderContext(Class<?> cls, String parentPath, String parentHttpMethod,
                         boolean readHidden) {
        setCls(cls);
        setParentPath(parentPath);
        setParentHttpMethod(parentHttpMethod);
        setReadHidden(readHidden);
    }


    public Class<?> getCls() {
        return cls;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getParentHttpMethod() {
        return parentHttpMethod;
    }

    public void setParentHttpMethod(String parentHttpMethod) {
        this.parentHttpMethod = parentHttpMethod;
    }

    public boolean isReadHidden() {
        return readHidden;
    }

    public void setReadHidden(boolean readHidden) {
        this.readHidden = readHidden;
    }

    public List<String> getParentConsumes() {
        return parentConsumes;
    }

    public void setParentConsumes(List<String> parentConsumes) {
        this.parentConsumes = parentConsumes;
    }

    public List<String> getParentProduces() {
        return parentProduces;
    }

    public void setParentProduces(List<String> parentProduces) {
        this.parentProduces = parentProduces;
    }

    public List<String> getParentTags() {
        return parentTags;
    }

    public void setParentTags(List<String> parentTags) {
        this.parentTags = parentTags;
    }

    public List<Parameter> getParentParameters() {
        return parentParameters;
    }

    public void setParentParameters(List<Parameter> parentParameters) {
        this.parentParameters = parentParameters;
    }
}
