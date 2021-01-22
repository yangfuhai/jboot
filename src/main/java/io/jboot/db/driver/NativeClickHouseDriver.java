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
