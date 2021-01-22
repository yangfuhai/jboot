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
package io.jboot.db.driver;

import com.github.housepower.jdbc.ClickHouseConnection;
import com.github.housepower.jdbc.ClickHouseDriver;
import com.github.housepower.jdbc.settings.ClickHouseConfig;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class NativeClickHouseDriver extends ClickHouseDriver {

    static {
        try {
            DriverManager.registerDriver(new NativeClickHouseDriver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ClickHouseConnection connect(String url, Properties properties) throws SQLException {
        if (!this.acceptsURL(url)) {
            return null;
        }

        ClickHouseConfig cfg = ClickHouseConfig.Builder.builder()
                .withJdbcUrl(url)
                .withProperties(properties)
                .build();
        return connect(url, cfg);
    }

    ClickHouseConnection connect(String url, ClickHouseConfig cfg) throws SQLException {
        if (!this.acceptsURL(url)) {
            return null;
        }
        return NativeClickHouseConnection.createClickHouseConnection(cfg.withJdbcUrl(url));
    }

}
