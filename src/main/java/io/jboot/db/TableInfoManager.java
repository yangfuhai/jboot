/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import java.util.ArrayList;
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


    /**
     * 获取 某数据源 下匹配的表
     *
     * @param dataSourceConfig
     * @return 该数据源下所有的表
     */
    public List<TableInfo> getMatchTablesInfos(DataSourceConfig dataSourceConfig) {

        Set<String> configTables = StrUtil.isNotBlank(dataSourceConfig.getTable())
                ? StrUtil.splitToSet(dataSourceConfig.getTable(), ",")
                : null;

        Set<String> configExTables = StrUtil.isNotBlank(dataSourceConfig.getExTable())
                ? StrUtil.splitToSet(dataSourceConfig.getExTable(), ",")
                : null;

        List<TableInfo> matchList = new ArrayList<>();

        for (TableInfo tableInfo : getAllTableInfos()) {

            //说明该表已经被指定到datasource了
            if (tableInfo.getDatasources() != null) {
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

            tableInfo.setDatasources(dataSourceConfig.getName());
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
        if (ArrayUtil.isNullOrEmpty(modelClassList)) {
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
            tableInfo.setPrimaryKey(AnnotationUtil.get(tb.primaryKey()));
            tableInfo.setTableName(AnnotationUtil.get(tb.tableName()));


            tableInfos.add(tableInfo);
        }

    }
}
