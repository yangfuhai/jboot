/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.StrUtil;

import java.util.*;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class TableInfoManager {

    private List<TableInfo> allTableInfos;


    private static TableInfoManager instance = new TableInfoManager();

    public static TableInfoManager me() {
        return instance;
    }


    /**
     * 初始化该数据下的 tableInfos 对象，其用来存储该数据源下有哪些表
     *
     * @param dataSourceConfig
     */
    public void initConfigMappingTables(DataSourceConfig dataSourceConfig) {

        // 该数据源下配置的所有表
        Set<String> configTables = StrUtil.isNotBlank(dataSourceConfig.getTable())
                ? StrUtil.splitToSetByComma(dataSourceConfig.getTable())
                : null;

        // 该数据源下排除的所有表
        Set<String> configExTables = StrUtil.isNotBlank(dataSourceConfig.getExTable())
                ? StrUtil.splitToSetByComma(dataSourceConfig.getExTable())
                : null;

        //所有的表信息
        List<TableInfo> allTableInfos = getAllTableInfos();


        for (TableInfo tableInfo : allTableInfos) {

            // 排除配置 jboot.datasource.extable 包含了这个表
            if (configExTables != null && configExTables.contains(tableInfo.getTableName())) {
                continue;
            }

            if (configTables != null && configTables.contains(tableInfo.getTableName())) {
                dataSourceConfig.addTableInfo(tableInfo, true);
            }

            if (tableInfo.getDatasourceNames().contains(dataSourceConfig.getName())) {
                dataSourceConfig.addTableInfo(tableInfo, true);
            }

            // 排除所有表，但允许当前数据源自己指定的表，指定的表不被排除
            if (configExTables != null && configExTables.contains("*")) {
                continue;
            }

            // 注解 @Table(datasource="xxxx") 指定了数据源，而且当前数据源未匹配
            if (!tableInfo.getDatasourceNames().isEmpty()) {
                continue;
            }

            // 如果当前的数据源已经配置了绑定的表，且未当前表未命中，不让其他表添加到当前数据源
            if (configTables != null && !configTables.isEmpty()) {
                continue;
            }

            dataSourceConfig.addTableInfo(tableInfo, false);
        }

    }

    private List<TableInfo> getAllTableInfos() {
        if (allTableInfos == null) {
            allTableInfos = new ArrayList<>();
            initTableInfos(allTableInfos);
        }
        return allTableInfos;
    }


    private void initTableInfos(List<TableInfo> tableInfoList) {
        List<Class<Model>> modelClassList = ClassScanner.scanSubClass(Model.class);
        if (ArrayUtil.isNullOrEmpty(modelClassList)) {
            return;
        }

        String scanPackage = JbootModelConfig.getConfig().getScanPackage();
        String unscanPackage = JbootModelConfig.getConfig().getUnscanPackage();

        for (Class<Model> clazz : modelClassList) {
            Table tb = clazz.getAnnotation(Table.class);
            if (tb == null) {
                continue;
            }

            if (StrUtil.isNotBlank(scanPackage)
                    && clazz.getName().startsWith(scanPackage.trim())) {
                addTable(tableInfoList, clazz, tb);
                continue;
            }


            if (StrUtil.isNotBlank(unscanPackage)
                    && ("*".equals(unscanPackage.trim()) || clazz.getName().startsWith(unscanPackage.trim()))) {
                continue;
            }

            addTable(tableInfoList, clazz, tb);
        }

    }

    private void addTable(List<TableInfo> tableInfoList, Class<Model> modelClass, Table tb) {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setModelClass(modelClass);
        tableInfo.setPrimaryKey(AnnotationUtil.get(tb.primaryKey()));
        tableInfo.setTableName(AnnotationUtil.get(tb.tableName()));
        tableInfo.setDatasource(AnnotationUtil.get(tb.datasource()));

        tableInfoList.add(tableInfo);
    }
}
