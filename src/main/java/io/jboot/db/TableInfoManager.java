/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.db;

import com.google.common.collect.Sets;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.db.annotation.Table;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.db.model.JbootModelConfig;
import io.jboot.kits.ArrayKits;
import io.jboot.kits.ClassScanner;
import io.jboot.kits.StringKits;

import java.util.*;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.db
 */
public class TableInfoManager {

    private List<TableInfo> allTableInfos;


    private static TableInfoManager instance = new TableInfoManager();

    public static TableInfoManager me() {
        return instance;
    }


    /**
     * 获取 某数据源 下匹配的表
     *
     * @param dataSourceConfig
     * @return 该数据源下所有的表
     */
    public List<TableInfo> getMatchTablesInfos(DataSourceConfig dataSourceConfig) {

        Set<String> configTables = StringKits.isNotBlank(dataSourceConfig.getTable())
                ? StringKits.splitToSet(dataSourceConfig.getTable(), ",")
                : null;

        Set<String> configExTables = StringKits.isNotBlank(dataSourceConfig.getExTable())
                ? StringKits.splitToSet(dataSourceConfig.getExTable(), ",")
                : null;

        List<TableInfo> matchList = new ArrayList<>();

        for (TableInfo tableInfo : getAllTableInfos()) {

            //该表已经被其他数据源优先使用了
            if (ArrayKits.isNotEmpty(tableInfo.getDatasources())) {
                continue;
            }

            if (configTables != null && configTables.contains(tableInfo.getTableName()) == false) {
                continue;
            }

            if (configExTables != null && configExTables.contains(tableInfo.getTableName())) {
                continue;
            }

            tableInfo.setDatasources(Sets.newHashSet(dataSourceConfig.getName()));
            matchList.add(tableInfo);
        }

        return matchList;
    }

    private List<TableInfo> getAllTableInfos() {
        if (allTableInfos == null) {
            allTableInfos = new ArrayList<>();
            initTableInfos(allTableInfos);
        }
        return allTableInfos;
    }


    private void initTableInfos(List<TableInfo> tableInfos) {
        List<Class<Model>> modelClassList = ClassScanner.scanSubClass(Model.class);
        if (ArrayKits.isNullOrEmpty(modelClassList)) {
            return;
        }

        String scanPackage = JbootModelConfig.getConfig().getScan();

        for (Class<Model> clazz : modelClassList) {
            Table tb = clazz.getAnnotation(Table.class);
            if (tb == null)
                continue;

            if (scanPackage != null && !clazz.getName().startsWith(scanPackage)) {
                continue;
            }

            Set<String> datasources = StringKits.isNotBlank(tb.datasource())
                    ? StringKits.splitToSet(tb.datasource(), ",")
                    : null;


            TableInfo tableInfo = new TableInfo();
            tableInfo.setModelClass(clazz);
            tableInfo.setPrimaryKey(tb.primaryKey());
            tableInfo.setTableName(tb.tableName());
            tableInfo.setDatasources(datasources);

            tableInfo.setActualDataNodes(tb.actualDataNodes());
            tableInfo.setDatabaseShardingStrategyConfig(tb.databaseShardingStrategyConfig());
            tableInfo.setTableShardingStrategyConfig(tb.tableShardingStrategyConfig());

            if (tb.keyGeneratorClass() != null && Void.class != tb.keyGeneratorClass()) {
                tableInfo.setKeyGeneratorClass(tb.keyGeneratorClass().getName());
            }
            tableInfo.setKeyGeneratorColumnName(tb.keyGeneratorColumnName());

            tableInfos.add(tableInfo);
        }

    }
}
