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
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.StrUtils;

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


    public List<TableInfo> getTablesInfos(DataSourceConfig dataSourceConfig) {
        List<TableInfo> tableInfos = new ArrayList<>();

        Set<String> configTables = null;
        if (StrUtils.isNotBlank(dataSourceConfig.getTable())) {
            configTables = StrUtils.splitToSet(dataSourceConfig.getTable(), ",");
        }

        Set<String> configExTables = StrUtils.splitToSet(dataSourceConfig.getExTable(), ",");

        for (TableInfo tableInfo : getAllTableInfos()) {

            //该表已经被其他数据源优先使用了
            if (ArrayUtils.isNotEmpty(tableInfo.getDatasources())) {
                continue;
            }

            if (configTables != null && configTables.contains(tableInfo.getTableName()) == false) {
                continue;
            }

            if (configExTables != null && configExTables.contains(tableInfo.getTableName())) {
                continue;
            }

            tableInfo.setDatasources(Sets.newHashSet(dataSourceConfig.getName()));
            tableInfos.add(tableInfo);
        }

        return tableInfos;
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
        if (ArrayUtils.isNullOrEmpty(modelClassList)) {
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

            Set<String> datasources = StrUtils.isNotBlank(tb.datasource())
                    ? StrUtils.splitToSet(tb.datasource(), ",")
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
