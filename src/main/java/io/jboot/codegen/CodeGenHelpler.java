/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.codegen;

import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.jboot.config.JbootProperties;
import io.jboot.db.datasource.DatasourceConfig;
import io.jboot.utils.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 代码生成工具类
 */
public class CodeGenHelpler {


    /**
     * 获取数据源
     *
     * @return
     */
    public static DataSource getDatasource() {
        DatasourceConfig datasourceConfig = JbootProperties.get(DatasourceConfig.class, "jboot.datasource");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(datasourceConfig.getUrl());
        config.setUsername(datasourceConfig.getUser());
        config.setPassword(datasourceConfig.getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setDriverClassName("com.mysql.jdbc.Driver");

        return new HikariDataSource(config);
    }


    /**
     * 排除指定的表，有些表不需要生成的
     *
     * @param list
     * @param excludeTables
     */
    public static void excludeTables(List<TableMeta> list, String excludeTables) {
        if (StringUtils.isNotBlank(excludeTables)) {
            List<TableMeta> newTableMetaList = new ArrayList<>();
            Set<String> excludeTableSet = StringUtils.splitToSet(excludeTables.toLowerCase(), ",");
            for (TableMeta tableMeta : list) {
                if (excludeTableSet.contains(tableMeta.name.toLowerCase())) {
                    System.out.println("exclude table : " + tableMeta.name);
                    continue;
                }
                newTableMetaList.add(tableMeta);
            }
            list.clear();
            list.addAll(newTableMetaList);
        }
    }


}


