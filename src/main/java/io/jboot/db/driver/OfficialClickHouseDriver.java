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

import com.google.common.collect.MapMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.clickhouse.ClickHouseConnection;
import ru.yandex.clickhouse.ClickHouseDriver;
import ru.yandex.clickhouse.settings.ClickHouseProperties;
import ru.yandex.clickhouse.util.LogProxy;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.*;

public class OfficialClickHouseDriver extends ClickHouseDriver {

    private static final Logger logger = LoggerFactory.getLogger(ClickHouseDriver.class);
    private static final ConcurrentMap<OfficialClickHouseConnection, Boolean> connections = new MapMaker().weakKeys().makeMap();

    static {
        try {
            DriverManager.registerDriver(new OfficialClickHouseDriver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public OfficialClickHouseDriver() {
        // 10 seconds
        scheduleConnectionsCleaning(10, TimeUnit.SECONDS);
    }


    @Override
    public ClickHouseConnection connect(String url, ClickHouseProperties properties) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }
        OfficialClickHouseConnection connection = new OfficialClickHouseConnection(url, properties);
        registerConnection(connection);
        return LogProxy.wrap(ClickHouseConnection.class, connection);
    }

    private void registerConnection(OfficialClickHouseConnection connection) {
        connections.put(connection, Boolean.TRUE);
    }


    /**
     * Schedules connections cleaning at a rate. Turned off by default.
     * See https://hc.apache.org/httpcomponents-client-4.5.x/tutorial/html/connmgmt.html#d5e418
     *
     * @param rate     period when checking would be performed
     * @param timeUnit time unit of rate
     */
    @Override
    public void scheduleConnectionsCleaning(int rate, TimeUnit timeUnit) {
        ScheduledConnectionCleaner.INSTANCE.scheduleAtFixedRate(() -> {
            try {
                for (OfficialClickHouseConnection connection : connections.keySet()) {
                    connection.cleanConnections();
                }
            } catch (Exception e) {
                logger.error("error evicting connections: " + e);
            }
        }, 0, rate, timeUnit);
    }


    static class ScheduledConnectionCleaner {
        static final ScheduledExecutorService INSTANCE = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());

        static class DaemonThreadFactory implements ThreadFactory {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = Executors.defaultThreadFactory().newThread(r);
                thread.setDaemon(true);
                return thread;
            }
        }
    }


}
