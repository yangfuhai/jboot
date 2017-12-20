/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.component.swagger;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.models.HttpMethod;
import io.swagger.models.Operation;
import io.swagger.models.Path;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 自定义 Swagger Path
 * @Description: 目的是为了 防止 fastjson 生成 opreations 和 operationMap 的json生成
 * @Package io.jboot.component.swagger
 */
public class SwaggerPath extends Path {

    @Override
    @JSONField(serialize = false)
    public List<Operation> getOperations() {
        return super.getOperations();
    }

    @Override
    @JSONField(serialize = false)
    public Map<HttpMethod, Operation> getOperationMap() {
        return super.getOperationMap();
    }
}
