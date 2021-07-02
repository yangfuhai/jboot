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
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.ColumnMeta;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.jboot.Jboot;
import io.jboot.codegen.CodeGenHelpler;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.db.driver.DriverClassNames;
import io.jboot.utils.DateUtil;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;

import javax.sql.DataSource;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.*;
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

        Map<String, Map<String, Object>> root = new LinkedHashMap<>();

        File file = new File(config.getJsonFilePathPathAbsolute());
        //如果文件存在，则先读取其配置，然后再修改
        if (file.exists()) {
            String oldJson = FileUtil.readString(file);
            JSONObject rootJsonObject = JSONObject.parseObject(oldJson);
            if (rootJsonObject != null && !rootJsonObject.isEmpty()) {
                for (String classOrSimpleName : rootJsonObject.keySet()) {
                    root.put(classOrSimpleName, rootJsonObject.getJSONObject(classOrSimpleName));
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
            Map<String, Object> classMockData = new HashMap<>();
            for (ColumnMeta columnMeta : tableMeta.columnMetas) {
                Object mockData = createMockData(columnMeta);
                if (mockData != null && !"".equals(mockData)) {
                    classMockData.put(columnMeta.attrName, mockData);
                }
            }
            if (!classMockData.isEmpty()) {
                root.put(StrKit.firstCharToLowerCase(tableMeta.modelName), classMockData);
            }
        }


        String jsonContent = JSONObject.toJSONString(root, true);
        FileUtil.writeString(file, jsonContent);

        System.out.println("Gen Remarks Json File ----->" + FileUtil.getCanonicalPath(file));
    }


    private static Object createMockData(ColumnMeta columnMeta) {
        if (String.class.getName().equals(columnMeta.javaType)) {
            return columnMeta.remarks;
        } else if (Date.class.getName().equals(columnMeta.javaType)) {
            return DateUtil.toDateTimeString(new Date());
        } else if (isTemporal(columnMeta.javaType)) {
            return DateUtil.toDateTimeString(new Date());
        } else if (int.class.getName().equals(columnMeta.javaType)) {
            return 1;
        } else if (Integer.class.getName().equals(columnMeta.javaType)) {
            return 100;
        } else if (long.class.getName().equals(columnMeta.javaType)) {
            return 1;
        } else if (Long.class.getName().equals(columnMeta.javaType)) {
            return 100;
        } else if (short.class.getName().equals(columnMeta.javaType)) {
            return 1;
        } else if (Short.class.getName().equals(columnMeta.javaType)) {
            return 100;
        } else if (BigDecimal.class.getName().equals(columnMeta.javaType)) {
            return BigDecimal.ONE;
        } else if (BigInteger.class.getName().equals(columnMeta.javaType)) {
            return BigDecimal.ONE;
        } else if (boolean.class.getName().equals(columnMeta.javaType)) {
            return Boolean.TRUE;
        } else if (Boolean.class.getName().equals(columnMeta.javaType)) {
            return Boolean.TRUE;
        } else if (float.class.getName().equals(columnMeta.javaType)) {
            return 1.0f;
        } else if (Float.class.getName().equals(columnMeta.javaType)) {
            return 1.0f;
        } else if (double.class.getName().equals(columnMeta.javaType)) {
            return 1.0d;
        } else if (Double.class.getName().equals(columnMeta.javaType)) {
            return 1.0d;
        } else {
            return "";
        }
    }

    private static boolean isTemporal(String javaType) {
        try {
            return Temporal.class.isAssignableFrom(Class.forName(javaType));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
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
                if (StrUtil.isNotBlank(columnMeta.remarks)) {
                    modelRemarks.put(columnMeta.attrName, columnMeta.remarks);
                }
            }
            if (!modelRemarks.isEmpty()) {
                root.put(StrKit.firstCharToLowerCase(tableMeta.modelName), modelRemarks);
            }
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
            return FileUtil.getCanonicalPath(new File(PathKit.getRootClassPath(), "../../" + jsonFilePath));
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
