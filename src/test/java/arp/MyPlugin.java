package arp;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.IDataSourceProvider;

import javax.sql.DataSource;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package arp
 */
public class MyPlugin extends ActiveRecordPlugin {
    public MyPlugin(String configName, DataSource dataSource, int transactionLevel) {
        super(configName, dataSource, transactionLevel);
    }


    public MyPlugin(DataSource dataSource) {
        super(dataSource);
    }

    public MyPlugin(String configName, DataSource dataSource) {
        super(configName, dataSource);
    }

    public MyPlugin(DataSource dataSource, int transactionLevel) {
        super(dataSource, transactionLevel);
    }

    public MyPlugin(String configName, IDataSourceProvider dataSourceProvider, int transactionLevel) {
        super(configName, dataSourceProvider, transactionLevel);
    }

    public MyPlugin(IDataSourceProvider dataSourceProvider) {
        super(dataSourceProvider);
    }

    public MyPlugin(String configName, IDataSourceProvider dataSourceProvider) {
        super(configName, dataSourceProvider);
    }

    public MyPlugin(IDataSourceProvider dataSourceProvider, int transactionLevel) {
        super(dataSourceProvider, transactionLevel);
    }

    public MyPlugin(Config config) {
        super(config);
    }
}
