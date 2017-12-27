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
package io.jboot.db.datasource;

import io.jboot.db.TableInfo;
import io.jboot.db.TableInfoManager;
import io.jboot.exception.JbootException;
import io.jboot.utils.ClassKits;
import io.jboot.utils.StringUtils;
import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.api.config.TableRuleConfiguration;
import io.shardingjdbc.core.api.config.strategy.ShardingStrategyConfiguration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class DataSourceBuilder {

    private DataSourceConfig datasourceConfig;

    public DataSourceBuilder(DataSourceConfig datasourceConfig) {
        this.datasourceConfig = datasourceConfig;
    }

    public DataSource build() {

        /**
         * 不启用分库分表功能
         */
        if (!datasourceConfig.isShardingEnable()) {
            return createDataSource(datasourceConfig);
        }


        Map<String, DataSource> dataSourceMap = new HashMap<>();

        /**
         * 如果包含了子数据源，说明是进行了分库
         */
        if (datasourceConfig.getChildDatasourceConfigs() != null) {
            for (DataSourceConfig childConfig : datasourceConfig.getChildDatasourceConfigs()) {
                dataSourceMap.put(childConfig.getName(), createDataSource(childConfig));
            }
        }
        /**
         * 可能只是分表，不分库
         */
        else {
            dataSourceMap.put(datasourceConfig.getName(), createDataSource(datasourceConfig));
        }


        //分库分表策略配置
        //具体需要配置信息可以参考：https://github.com/shardingjdbc/sharding-jdbc-example/tree/dev/sharding-jdbc-raw-jdbc-example/sharding-jdbc-raw-jdbc-java-example/src/main/java/io/shardingjdbc/example/jdbc/java
        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();

        //通过数据源配置，获取所有的表信息
        List<TableInfo> tableInfos = TableInfoManager.me().getTablesInfos(datasourceConfig.getTable(), datasourceConfig.getExcludeTable());

        //具体的表，例如："t_order, t_order_item"
        StringBuilder bindTableGroups = new StringBuilder();

        for (TableInfo ti : tableInfos) {
            //每张表的分表和分库规则
            TableRuleConfiguration tableRuleConfiguration = getTableRuleConfiguration(ti);
            shardingRuleConfiguration.getTableRuleConfigs().add(tableRuleConfiguration);

            bindTableGroups.append(ti.getTableName()).append(",");
        }

        if (bindTableGroups.length() > 0) {
            bindTableGroups.deleteCharAt(bindTableGroups.length() - 1); //delete last char
            shardingRuleConfiguration.getBindingTableGroups().add(bindTableGroups.toString());
        }


        try {
            return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfiguration, new HashMap<>(), new Properties());
        } catch (SQLException e) {
            throw new JbootException(e);
        }


    }

    /**
     * 获取每张表的分表策略配置
     *
     * @param tableInfo
     * @return
     */
    private static TableRuleConfiguration getTableRuleConfiguration(TableInfo tableInfo) {
        TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration();

        //逻辑表
        tableRuleConfig.setLogicTable(tableInfo.getTableName());

        //真实表
        if (StringUtils.isNotBlank(tableInfo.getActualDataNodes())) {
            tableRuleConfig.setActualDataNodes(tableInfo.getActualDataNodes());
        }

        if (StringUtils.isNotBlank(tableInfo.getKeyGeneratorClass())) {
            tableRuleConfig.setKeyGeneratorClass(tableInfo.getKeyGeneratorClass());
        }

        if (StringUtils.isNotBlank(tableInfo.getKeyGeneratorColumnName())) {
            tableRuleConfig.setKeyGeneratorColumnName(tableInfo.getKeyGeneratorColumnName());
        }

        //分库规则
        if (tableInfo.getDatabaseShardingStrategyConfig() != ShardingStrategyConfiguration.class) {
            tableRuleConfig.setDatabaseShardingStrategyConfig(ClassKits.newInstance(tableInfo.getDatabaseShardingStrategyConfig()));
        }

        //分表规则
        if (tableInfo.getTableShardingStrategyConfig() != ShardingStrategyConfiguration.class) {
            tableRuleConfig.setTableShardingStrategyConfig(ClassKits.newInstance(tableInfo.getTableShardingStrategyConfig()));
        }

        return tableRuleConfig;
    }


    private DataSource createDataSource(DataSourceConfig dataSourceConfig) {
        
        //支持自定义 数据源，默认为hikariCP，可以通过这个扩展druid、c3p0等
        DataSourceFactory factory = ClassKits.newInstance(dataSourceConfig.getFactory());
        if (factory == null) {
            factory = new HikariDataSourceFactory();
        }

        return factory.createDataSource(dataSourceConfig);
    }
}
