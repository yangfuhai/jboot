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

import com.jfinal.plugin.activerecord.Model;
import io.jboot.db.annotation.Table;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.db.model.JbootModelConfig;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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


    public List<TableInfo> getTablesInfos(String datasourceName) {
        List<TableInfo> tableInfos = new ArrayList<>();

        for (TableInfo tableInfo : getAllTableInfos()) {
            if (tableInfo.getDatasources().contains(datasourceName)) {
                tableInfos.add(tableInfo);
            }
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

            Set<String> datasources = new HashSet<>();
            if (StringUtils.isNotBlank(tb.datasource())) {
                datasources.addAll(StringUtils.splitToSet(tb.datasource(), ","));
            } else {
                datasources.add(DataSourceConfig.NAME_DEFAULT);
            }

            if (StringUtils.isNotBlank(tb.exDatasource())) {
                Set<String> exDatasources = StringUtils.splitToSet(tb.exDatasource(), ",");
                for (String exDatasource : exDatasources) {
                    datasources.remove(exDatasource);
                }
            }

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
