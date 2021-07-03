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

import com.alibaba.fastjson.JSONObject;
import io.jboot.apidoc.annotation.ApiResp;
import io.jboot.utils.ClassType;
import io.jboot.utils.StrUtil;

import java.io.Serializable;
import java.util.Objects;

public class ApiResponse implements Serializable {

    private String name;
    private String dataType;
    private ClassType classType;
    private String remarks;
    private String mock;


    public ApiResponse() {
    }

    public ApiResponse(ApiResp apiResp) {
        this.name = apiResp.name();
        this.dataType = apiResp.dataType().getSimpleName();
        this.classType = new ClassType(apiResp.dataType(), apiResp.genericTypes());
        this.remarks = apiResp.notes();
        this.mock = apiResp.mock();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getMock() {
        return mock;
    }

    public Object getMockObject() {
        if (StrUtil.isBlank(mock)) {
            return "";
        }
        if ((mock.startsWith("{") && mock.endsWith("}")) || (mock.startsWith("[") && mock.endsWith("]"))) {
            return JSONObject.parse(mock);
        } else {
            return mock;
        }
    }

    public void setMock(String mock) {
        this.mock = mock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiResponse that = (ApiResponse) o;
        return Objects.equals(name, that.name);
    }

}
