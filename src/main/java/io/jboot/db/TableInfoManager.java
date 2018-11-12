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


    /**
     * 获取某个datasrource 配置下对于有哪些表，一个DataSource 可能有多个表，一个表也可能对于对个 DataSource
     *
     * @param dataSourceConfig
     * @return
     */
    public List<TableInfo> getConfigTables(DataSourceConfig dataSourceConfig) {

        List<TableInfo> tableInfos = new ArrayList<>();

        // 数据源指定包含的表
        Set<String> configTables = StrUtils.isNotBlank(dataSourceConfig.getTable())
                ? StrUtils.splitToSet(dataSourceConfig.getTable(), ",")
                : null;

        // 数据源指定排除的表
        Set<String> configExTables = StrUtils.splitToSet(dataSourceConfig.getExTable(), ",");


        // model 通过 @Table 配置指定的表
        for (TableInfo tableInfo : getAllTableInfos()) {

            //说明该表已经被指定到datasource了
            if (tableInfo.getDatasrouces() != null) {
                continue;
            }

            // 如果 datasource.table 已经配置了，
            // 就只用这个配置的，不是这个配置的都排除
            if (configTables != null && !configTables.contains(tableInfo.getTableName())) {
                continue;
            }

            //被指定排除的表进行排除了
            if (configExTables != null && configExTables.contains(tableInfo.getTableName())) {
                continue;
            }

            tableInfo.setDatasrouces(dataSourceConfig.getName());
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


            TableInfo tableInfo = new TableInfo();
            tableInfo.setModelClass(clazz);
            tableInfo.setPrimaryKey(tb.primaryKey());
            tableInfo.setTableName(tb.tableName());

            
            tableInfos.add(tableInfo);
        }

    }
}
