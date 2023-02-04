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
package io.jboot.db.datasource;

import com.jfinal.kit.PathKit;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.exception.JbootException;
import io.jboot.support.seata.JbootSeataManager;
import io.jboot.utils.StrUtil;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;

import javax.sql.DataSource;
import java.io.File;


public class DataSourceBuilder {

    private DataSourceConfig config;

    public DataSourceBuilder(DataSourceConfig datasourceConfig) {
        this.config = datasourceConfig;
    }

    public DataSource build() {

        String shardingConfigYaml = config.getShardingConfigYaml();

        // 不启用分库分表的配置
        if (StrUtil.isBlank(shardingConfigYaml)) {
            DataSource ds = createDataSource(config);
            return JbootSeataManager.me().wrapDataSource(ds);
        }


        File yamlFile = shardingConfigYaml.startsWith(File.separator)
                ? new File(shardingConfigYaml)
                : new File(PathKit.getRootClassPath(), shardingConfigYaml);

        try {
//            return YamlShardingDataSourceFactory.createDataSource(yamlFile);
            return YamlShardingSphereDataSourceFactory.createDataSource(yamlFile);
        } catch (Exception e) {
            throw new JbootException(e);
        }
    }


    private DataSource createDataSource(DataSourceConfig dsc) {

        String factory = dsc.getFactory();
        if (StrUtil.isBlank(factory)) {
            return new HikariDataSourceFactory().createDataSource(dsc);
        }

        switch (factory) {
            case "hikari":
            case "hikariCP":
            case "hikaricp":
                return new HikariDataSourceFactory().createDataSource(dsc);
            case "druid":
                return new DruidDataSourceFactory().createDataSource(dsc);
            default:
                DataSourceFactory dataSourceFactory = JbootSpiLoader.load(DataSourceFactory.class, factory);
                if (dataSourceFactory == null) {
                    throw new NullPointerException("Can not load DataSourceFactory spi for name: " + factory);
                }
                return dataSourceFactory.createDataSource(dsc);
        }
    }
}
