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
package io.jboot.support.sentinel;

import com.alibaba.csp.sentinel.datasource.FileWritableDataSource;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.PathKit;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.support.sentinel.datasource.*;
import io.jboot.utils.StrUtil;

import java.io.File;
import java.util.List;

public class JbootSentinelBuilder {

    public void init() {
        SentinelConfig config = SentinelConfig.get();

        // 初始化 sentinel 数据源，
        // 当配置数据源的时候，sentinel 控制面板的配置将会更新的时候，无法写入到数据源的，需要去实现主动写入，这是 Sentinel 的一个坑
        // todo 晚点实现主动 Sentinel 控制台写入到数据源
        if (StrUtil.isNotBlank(config.getDatasource())) {
            SentinelDatasourceFactory factory = getDatasourceFactory(config);
            ReadableDataSource rds = factory.createDataSource();
            FlowRuleManager.register2Property(rds.getProperty());
        }

        // 当未配置数据源的情况下，使用文件数据源
        // 将可写数据源注册至 transport 模块的 WritableDataSourceRegistry 中.
        // 这样收到控制台推送的规则时，Sentinel 会先更新到内存，然后将规则写入到文件中.
        // 文档：https://github.com/alibaba/Sentinel/wiki/%E5%9C%A8%E7%94%9F%E4%BA%A7%E7%8E%AF%E5%A2%83%E4%B8%AD%E4%BD%BF%E7%94%A8-Sentinel
        else {

            String rulePath = config.getRuleFile();
            File ruleFile = rulePath.startsWith("/") ? new File(rulePath) : new File(PathKit.getWebRootPath(), rulePath);

            ReadableDataSource rds = new FileDataSource<>(ruleFile, this::decodeJson);
            FlowRuleManager.register2Property(rds.getProperty());

            WritableDataSource<List<FlowRule>> wds = new FileWritableDataSource<>(ruleFile, this::encodeJson);
            WritableDataSourceRegistry.registerFlowDataSource(wds);
        }

    }

    private <T> String encodeJson(T t) {
        return JSON.toJSONString(t);
    }

    private List<FlowRule> decodeJson(String source) {
        return JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
        });
    }


    private SentinelDatasourceFactory getDatasourceFactory(SentinelConfig config) {
        String datasource = config.getDatasource();
        switch (datasource) {
            case SentinelConfig.DATASOURCE_APOLLO:
                return new ApolloDatasourceFactory();
            case SentinelConfig.DATASOURCE_NACOS:
                return new NacosDatasourceFactory();
            case SentinelConfig.DATASOURCE_ZOOKEEPER:
                return new ZookeeperDatasourceFactory();
            case SentinelConfig.DATASOURCE_REDIS:
                return new RedisDatasourceFactory();
            default:
                SentinelDatasourceFactory dataSourceFactory = JbootSpiLoader.load(SentinelDatasourceFactory.class, datasource);
                if (dataSourceFactory == null) {
                    throw new NullPointerException("Can not load SentinelDatasourceFactory spi for name: " + datasource);
                }
                return dataSourceFactory;
        }
    }


}
