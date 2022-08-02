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
package io.jboot.db.driver;

import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

import java.util.HashMap;
import java.util.Map;

public class DriverClassNames {

    private static final Map<String, String[]> driverClassNames = new HashMap<>();

    static {
        driverClassNames.put(DataSourceConfig.TYPE_MYSQL, new String[]{"com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver"});
        driverClassNames.put(DataSourceConfig.TYPE_ORACLE, new String[]{"oracle.jdbc.driver.OracleDriver", "oracle.jdbc.OracleDriver"});
        driverClassNames.put(DataSourceConfig.TYPE_SQLSERVER, new String[]{"com.microsoft.sqlserver.jdbc.SQLServerDriver"});
        driverClassNames.put(DataSourceConfig.TYPE_SQLITE, new String[]{"org.sqlite.JDBC"});
        driverClassNames.put(DataSourceConfig.TYPE_POSTGRESQL, new String[]{"org.postgresql.Driver"});
        driverClassNames.put(DataSourceConfig.TYPE_DM, new String[]{"dm.jdbc.driver.DmDriver"});
        driverClassNames.put(DataSourceConfig.TYPE_CLICKHOUSE, new String[]{"com.github.housepower.jdbc.ClickHouseDriver", "ru.yandex.clickhouse.ClickHouseDriver"});
        driverClassNames.put(DataSourceConfig.TYPE_INFORMIX, new String[]{"com.informix.jdbc.IfxDriver"});
    }


    /**
     * Jboot 自己实现的驱动，比如 ClickHouse 为了适配 JFinal 做了一些驱动改动
     */
    private static final Map<String, String> jbootDriverMapping = new HashMap<>();

    static {
        jbootDriverMapping.put("com.github.housepower.jdbc.ClickHouseDriver", "io.jboot.db.driver.NativeClickHouseDriver");
        jbootDriverMapping.put("ru.yandex.clickhouse.ClickHouseDriver", "io.jboot.db.driver.OfficialClickHouseDriver");
    }


    /**
     * 获取 默认的 jdbc 驱动类
     *
     * @param type
     * @return
     */
    public static String getDefaultDriverClass(String type) {
        String[] drivers = driverClassNames.get(type.toLowerCase());
        if (drivers == null || drivers.length == 0) {
            return null;
        }

        for (String driver : drivers) {
            if (ClassUtil.hasClass(driver)) {
                String jbootDriver = jbootDriverMapping.get(driver);
                return StrUtil.isNotBlank(jbootDriver) ? jbootDriver : driver;
            }
        }
        return null;
    }


}
