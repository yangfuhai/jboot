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
package io.jboot.apidoc;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.ColumnMeta;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.jboot.Jboot;
import io.jboot.codegen.CodeGenHelpler;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.db.driver.DriverClassNames;
import io.jboot.utils.FileUtil;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ApiJsonGenerator {

    /**
     * 生成 Mock Json 数据
     */
    public static void genMockJson() {
        genMockJson(new JsonGeneratorConfig("api-mock.json"));
    }


    /**
     * 生成 Mock Json 数据
     *
     * @param config
     */
    public static void genMockJson(JsonGeneratorConfig config) {


        File file = new File(config.getJsonFilePathPathAbsolute());
        //如果文件存在，则先读取其配置，然后再修改
        if (file.exists()) {
            System.err.println("genMockJson() do Nothing, file exists: " + FileUtil.getCanonicalPath(file));
            return;
        }

        MetaBuilder builder = CodeGenHelpler.createMetaBuilder(config.getDatasource(), config.getType(), false);
        List<TableMeta> tableMetas = builder.build();

        if (config.tableMetaFilter != null) {
            tableMetas = tableMetas.stream()
                    .filter(config.tableMetaFilter)
                    .collect(Collectors.toList());
        }

        Map<String, Map<String, String>> root = new LinkedHashMap<>();


        for (TableMeta tableMeta : tableMetas) {
            Map<String, String> modelRemarks = new HashMap<>();
            for (ColumnMeta columnMeta : tableMeta.columnMetas) {
                modelRemarks.put(columnMeta.attrName, columnMeta.remarks);
            }
            root.put(tableMeta.modelName, modelRemarks);
        }


        String jsonContent = JSONObject.toJSONString(root, true);
        FileUtil.writeString(file, jsonContent);

        System.out.println("Gen Remarks Json File ----->" + FileUtil.getCanonicalPath(file));
    }

    /**
     * 生成 Model 的字段备注数据
     */
    public static void genRemarksJson() {
        genRemarksJson(new JsonGeneratorConfig("api-remarks.json"));
    }

    /**
     * 生成 Model 的字段备注数据
     */
    public static void genRemarksJson(JsonGeneratorConfig config) {

        Map<String, Map<String, String>> root = new LinkedHashMap<>();

        File file = new File(config.getJsonFilePathPathAbsolute());
        //如果文件存在，则先读取其配置，然后再修改
        if (file.exists()) {
            String oldJson = FileUtil.readString(file);
            JSONObject rootJsonObject = JSONObject.parseObject(oldJson);
            if (rootJsonObject != null && !rootJsonObject.isEmpty()) {
                for (String classOrSimpleName : rootJsonObject.keySet()) {
                    Map<String, String> remarks = new HashMap<>();
                    JSONObject modelRemarks = rootJsonObject.getJSONObject(classOrSimpleName);
                    modelRemarks.forEach((k, v) -> remarks.put(k, String.valueOf(v)));
                    root.put(classOrSimpleName, remarks);
                }
            }
        }


        MetaBuilder builder = CodeGenHelpler.createMetaBuilder(config.getDatasource(), config.getType(), false);
        List<TableMeta> tableMetas = builder.build();

        if (config.tableMetaFilter != null) {
            tableMetas = tableMetas.stream()
                    .filter(config.tableMetaFilter)
                    .collect(Collectors.toList());
        }


        for (TableMeta tableMeta : tableMetas) {
            Map<String, String> modelRemarks = new HashMap<>();
            for (ColumnMeta columnMeta : tableMeta.columnMetas) {
                modelRemarks.put(columnMeta.attrName, columnMeta.remarks);
            }
            root.put(tableMeta.modelName, modelRemarks);
        }


        String jsonContent = JSONObject.toJSONString(root, true);
        FileUtil.writeString(file, jsonContent);

        System.out.println("Gen Remarks Json File ----->" + FileUtil.getCanonicalPath(file));
    }


    public static class JsonGeneratorConfig {

        private boolean useJbootDatasource = true;

        private String jdbcUrl;
        private String userName;
        private String password;
        private String type = DataSourceConfig.TYPE_MYSQL;

        private String jsonFilePath;
        private Predicate<TableMeta> tableMetaFilter;

        public JsonGeneratorConfig() {
        }

        public JsonGeneratorConfig(String jsonFilePath) {
            this.jsonFilePath = jsonFilePath;
        }

        public boolean isUseJbootDatasource() {
            return useJbootDatasource;
        }

        public void setUseJbootDatasource(boolean useJbootDatasource) {
            this.useJbootDatasource = useJbootDatasource;
        }

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getJsonFilePath() {
            return jsonFilePath;
        }

        public void setJsonFilePath(String jsonFilePath) {
            this.jsonFilePath = jsonFilePath;
        }

        public String getJsonFilePathPathAbsolute() {
            if (FileUtil.isAbsolutePath(jsonFilePath)) {
                return jsonFilePath;
            }
            return new File(PathKit.getRootClassPath(), "../../" + jsonFilePath).getAbsolutePath();
        }


        public Predicate<TableMeta> getTableMetaFilter() {
            return tableMetaFilter;
        }

        public void setTableMetaFilter(Predicate<TableMeta> tableMetaFilter) {
            this.tableMetaFilter = tableMetaFilter;
        }


        public DataSource getDatasource() {
            if (useJbootDatasource) {
                DataSourceConfig datasourceConfig = Jboot.config(DataSourceConfig.class, "jboot.datasource");
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(datasourceConfig.getUrl());
                config.setUsername(datasourceConfig.getUser());
                config.setPassword(datasourceConfig.getPassword());
                config.setDriverClassName(datasourceConfig.getDriverClassName());

                return new HikariDataSource(config);
            } else {
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(this.jdbcUrl);
                config.setUsername(this.userName);
                config.setPassword(this.password);
                config.setDriverClassName(DriverClassNames.getDefaultDriverClass(this.type));

                return new HikariDataSource(config);
            }
        }
    }
}
