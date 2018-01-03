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
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.db
 */
public class TableInfoManager {

    private List<TableInfo> tableInfos;


    private static TableInfoManager instance = new TableInfoManager();

    public static TableInfoManager me() {
        return instance;
    }

    public List<TableInfo> getAllTableInfos() {
        if (tableInfos == null) {
            tableInfos = new ArrayList<>();
            initTableInfos(tableInfos);
        }
        return tableInfos;
    }


    public List<TableInfo> getTablesInfos(String includeTables, String excludeTables) {
        List<TableInfo> tableInfos = new ArrayList<>();

        Set<String> includeTableSet = includeTables == null ? null : StringUtils.splitToSet(includeTables, ",");
        Set<String> excludeTableSet = excludeTables == null ? null : StringUtils.splitToSet(excludeTables, ",");

        for (TableInfo tableInfo : getAllTableInfos()) {
            boolean isAdd = false;
            if (includeTableSet == null || includeTableSet.isEmpty()) {
                isAdd = true;
            } else if (includeTableSet.contains(tableInfo.getTableName())) {
                isAdd = true;
            }

            if (isAdd == true && excludeTableSet != null && excludeTableSet.contains(tableInfo.getTableName())) {
                isAdd = false;
            }

            if (isAdd) {
                tableInfos.add(tableInfo);
            }
        }

        return tableInfos;
    }


    private void initTableInfos(List<TableInfo> tableInfos) {
        List<Class<Model>> modelClassList = ClassScanner.scanSubClass(Model.class);
        if (ArrayUtils.isNullOrEmpty(modelClassList)) {
            return;
        }

        for (Class<Model> clazz : modelClassList) {
            Table tb = clazz.getAnnotation(Table.class);
            if (tb == null)
                continue;


            TableInfo tableInfo = new TableInfo();
            tableInfo.setModelClass(clazz);
            tableInfo.setPrimaryKey(tb.primaryKey());
            tableInfo.setTableName(tb.tableName());

            tableInfo.setActualDataNodes(tb.actualDataNodes());
            tableInfo.setDatabaseShardingStrategyConfig(tb.databaseShardingStrategyConfig());
            tableInfo.setTableShardingStrategyConfig(tb.tableShardingStrategyConfig());

            tableInfo.setKeyGeneratorClass(tb.keyGeneratorClass());
            tableInfo.setKeyGeneratorColumnName(tb.keyGeneratorColumnName());

            tableInfos.add(tableInfo);
        }

    }
}
