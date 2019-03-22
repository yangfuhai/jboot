package io.jboot.support.fescar.datasoucre;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fescar.rm.datasource.DataSourceProxy;
import com.google.common.collect.Sets;
import com.jfinal.log.Log;
import io.jboot.core.spi.JbootSpi;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.db.datasource.DataSourceFactory;

import java.sql.SQLException;

@JbootSpi("fescar")
public class FescarDataSourceProxyFactory implements DataSourceFactory {
	static Log log = Log.getLog(FescarDataSourceProxyFactory.class);

	public DataSourceProxy createDataSource(DataSourceConfig config) {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setUrl(config.getUrl());
		druidDataSource.setUsername(config.getUser());
		druidDataSource.setPassword(config.getPassword());
		druidDataSource.setDriverClassName(config.getDriverClassName());
		druidDataSource.setMaxActive(config.getMaximumPoolSize());

		if (config.getMinimumIdle() != null) {
			druidDataSource.setMinIdle(config.getMinimumIdle());
		}

		if (config.getConnectionInitSql() != null) {
			druidDataSource.setConnectionInitSqls(Sets.newHashSet(config.getConnectionInitSql()));
		}

		try {
			druidDataSource.setFilters("stat");
		} catch (SQLException e) {
			log.error("FescarDataSourceProxyFactory is error", e);
		}
		return new DataSourceProxy(druidDataSource);
	}

}
