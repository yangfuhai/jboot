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
package io.jboot.support.sentinel.datasource;

import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.jboot.Jboot;

import java.util.List;

public class NacosDatasourceFactory implements SentinelDatasourceFactory {

    @Override
    public ReadableDataSource createDataSource() {
        NacosDatasourceConfig nds = Jboot.config(NacosDatasourceConfig.class);
        nds.assertConfigOk();

        return new NacosDataSource<>(nds.getServerAddress(), nds.getGroupId(), nds.getDataId(),
                source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                }));
    }
}
