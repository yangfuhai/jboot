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
import com.alibaba.csp.sentinel.datasource.redis.RedisDataSource;
import com.alibaba.csp.sentinel.datasource.redis.config.RedisConnectionConfig;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.jboot.Jboot;
import io.jboot.utils.StrUtil;

import java.util.List;

public class RedisDatasourceFactory implements SentinelDatasourceFactory {

    @Override
    public ReadableDataSource createDataSource() {
        RedisDatasourceConfig rdc = Jboot.config(RedisDatasourceConfig.class);
        rdc.assertConfigOk();

        RedisConnectionConfig.Builder builder = RedisConnectionConfig.builder()
                .withHost(rdc.getHost())
                .withPort(rdc.getPort())
                .withDatabase(rdc.getDatabase());

        if (StrUtil.isNotBlank(rdc.getPassword())) {
            builder.withPassword(rdc.getPassword());
        }

        return new RedisDataSource<>(builder.build(), rdc.getRuleKey(), rdc.getChannel(),
                source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                }));

    }
}
