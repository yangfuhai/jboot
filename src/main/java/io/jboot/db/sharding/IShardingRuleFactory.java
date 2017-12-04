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
package io.jboot.db.sharding;


import io.shardingjdbc.core.rule.ShardingRule;

import javax.sql.DataSource;

/**
 * ShardingRule 工厂，用于生产 分配规则
 */
public interface IShardingRuleFactory {

    /**
     * 创建 分库分表规则
     * 具体的规则 可以才考 ： https://github.com/shardingjdbc/sharding-jdbc/tree/master/sharding-jdbc-example/sharding-jdbc-example-jdbc
     * <p>
     * 在Jboot中，可以使用多数据源的替代分库，在这里只需要配置分表规则就可以了
     *
     * @param originalDataSource 原本的数据源
     * @return 分片规则，用户可以忽略原本的数据源，而使用自己的数据源
     */
    public ShardingRule createShardingRule(DataSource originalDataSource);
}
