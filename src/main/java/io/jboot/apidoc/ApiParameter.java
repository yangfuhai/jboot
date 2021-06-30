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

import io.jboot.apidoc.annotation.ApiPara;
import io.jboot.utils.StrUtil;

import java.lang.reflect.Type;

public class ApiParameter {

    private String name;
    private String value;
    private String notes;

    private Class<?> dataType;
    private Type dataGenericType;


    private HttpMethod[] httpMethods;
    private Boolean require;
    private Boolean notBlank;
    private Boolean notEmpty;
    private Boolean email;
    private Long min;
    private Long max;
    private String pattern;
    private String defaultValue;

    public ApiParameter() {
    }

    public ApiParameter(ApiPara apiPara, HttpMethod[] defaultMethods) {
        this.name = apiPara.name();
        this.value = apiPara.value();
        this.notes = apiPara.notes();
        this.dataType = apiPara.dataType();
        this.httpMethods = apiPara.method().length == 0 ? defaultMethods : apiPara.method();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Class<?> getDataType() {
        return dataType;
    }

    public void setDataType(Class<?> dataType) {
        this.dataType = dataType;
    }

    public Type getDataGenericType() {
        return dataGenericType;
    }

    public void setDataGenericType(Type dataGenericType) {
        this.dataGenericType = dataGenericType;
    }

    public HttpMethod[] getHttpMethods() {
        return httpMethods;
    }

    public void setHttpMethods(HttpMethod[] httpMethods) {
        this.httpMethods = httpMethods;
    }

    public Boolean getRequire() {
        return require;
    }

    public void setRequire(Boolean require) {
        this.require = require;
    }

    public Boolean getNotBlank() {
        return notBlank;
    }

    public void setNotBlank(Boolean notBlank) {
        this.notBlank = notBlank;
    }

    public Boolean getNotEmpty() {
        return notEmpty;
    }

    public void setNotEmpty(Boolean notEmpty) {
        this.notEmpty = notEmpty;
    }

    public Boolean getEmail() {
        return email;
    }

    public void setEmail(Boolean email) {
        this.email = email;
    }

    public Long getMin() {
        return min;
    }

    public void setMin(Long min) {
        this.min = min;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getHttpMethodsString() {
        StringBuilder sb = new StringBuilder();
        if (httpMethods != null) {
            for (int i = 0; i < httpMethods.length; i++) {
                sb.append(httpMethods[i].getValue());
                if (i != httpMethods.length - 1) {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }

    public String getNotesString() {
        StringBuilder sb = new StringBuilder();
        if (StrUtil.isNotBlank(defaultValue)) {
            sb.append("默认值：" + defaultValue);
        }

        if (StrUtil.isNotBlank(notes)) {
            if (sb.length() > 0) {
                sb.append(" ；");
            }
            sb.append(notes);
        }
        return sb.toString();
    }
}
