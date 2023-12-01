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
package io.jboot.codegen;

import com.jfinal.plugin.activerecord.dialect.*;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.jboot.Jboot;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.StrUtil;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 代码生成工具类
 */
public class CodeGenHelpler {


    public static String getUserDir() {
        return System.getProperty("user.dir");
    }


    /**
     * 获取数据源
     *
     * @return
     */
    public static DataSource getDatasource() {
        DataSourceConfig datasourceConfig = Jboot.config(DataSourceConfig.class, "jboot.datasource");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(datasourceConfig.getUrl());
        config.setUsername(datasourceConfig.getUser());
        config.setPassword(datasourceConfig.getPassword());
        config.setDriverClassName(datasourceConfig.getDriverClassName());

        return new HikariDataSource(config);
    }


    public static MetaBuilder createMetaBuilder() {
        return createMetaBuilder(getDatasource(), Jboot.config(DataSourceConfig.class, "jboot.datasource").getType());
    }


    public static MetaBuilder createMetaBuilder(DataSource dataSource) {
        return createMetaBuilder(dataSource, DataSourceConfig.TYPE_MYSQL);
    }


    public static MetaBuilder createMetaBuilder(DataSource dataSource, String type) {
        return createMetaBuilder(dataSource, type, true);
    }

    public static MetaBuilder createMetaBuilder(DataSource dataSource, String type, boolean removeNoPrimaryKeyTable) {
        MetaBuilder metaBuilder = removeNoPrimaryKeyTable ? new MetaBuilder(dataSource) : new MetaBuilder(dataSource) {
            @Override
            protected void removeNoPrimaryKeyTable(List<TableMeta> ret) {
                //do Nothing...
            }
        };
        metaBuilder.setGenerateRemarks(true);
        switch (type) {
            case DataSourceConfig.TYPE_MYSQL:
                metaBuilder.setDialect(new MysqlDialect());
                break;
            case DataSourceConfig.TYPE_ORACLE:
                metaBuilder.setDialect(new OracleDialect());
                break;
            case DataSourceConfig.TYPE_SQLSERVER:
                metaBuilder.setDialect(new SqlServerDialect());
                break;
            case DataSourceConfig.TYPE_SQLITE:
                metaBuilder.setDialect(new Sqlite3Dialect());
                break;
            case DataSourceConfig.TYPE_ANSISQL:
                metaBuilder.setDialect(new AnsiSqlDialect());
                break;
            case DataSourceConfig.TYPE_POSTGRESQL:
                metaBuilder.setDialect(new PostgreSqlDialect());
                break;
            case DataSourceConfig.TYPE_INFORMIX:
                metaBuilder.setDialect(new InformixDialect());
                break;
            default:
                throw new JbootIllegalConfigException("Only support datasource type: mysql、oracle、sqlserver、sqlite、ansisql、postgresql and infomix" +
                        ", please check your jboot.properties. ");
        }

        return metaBuilder;
    }


    /**
     * 排除指定的表，有些表不需要生成的
     *
     * @param list
     * @param excludeTables
     */
    public static void excludeTables(List<TableMeta> list, String excludeTables) {
        if (StrUtil.isNotBlank(excludeTables)) {
            List<TableMeta> newTableMetaList = new ArrayList<>();
            Set<String> excludeTableSet = StrUtil.splitToSet(excludeTables.toLowerCase(), ",");
            for (TableMeta tableMeta : list) {
                if (excludeTableSet.contains(tableMeta.name.toLowerCase())) {
                    System.out.println("exclude table: " + tableMeta.name);
                    continue;
                }
                newTableMetaList.add(tableMeta);
            }
            list.clear();
            list.addAll(newTableMetaList);
        }
    }


}


