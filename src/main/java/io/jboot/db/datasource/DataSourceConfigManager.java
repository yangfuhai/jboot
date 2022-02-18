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

import com.google.common.collect.Maps;
import io.jboot.utils.ConfigUtil;
import io.jboot.utils.StrUtil;

import java.util.Map;

public class DataSourceConfigManager {

    private static final String DATASOURCE_PREFIX = "jboot.datasource.";


    private static DataSourceConfigManager manager = new DataSourceConfigManager();

    public static DataSourceConfigManager me() {
        return manager;
    }

    private Map<String, DataSourceConfig> datasourceConfigs = Maps.newHashMap();

    private DataSourceConfigManager() {

        Map<String,DataSourceConfig> configMap = ConfigUtil.getConfigModels(DataSourceConfig.class,"jboot.datasource");

        for (Map.Entry<String,DataSourceConfig> entry : configMap.entrySet()){
            DataSourceConfig config = entry.getValue();

            //默认数据源
            if (entry.getKey().equals("default") && StrUtil.isBlank(config.getName())){
                config.setName(DataSourceConfig.NAME_DEFAULT);
            }else if (StrUtil.isBlank(config.getName())){
                config.setName(entry.getKey());
            }

            addConfig(config);
        }
    }

    public void addConfig(DataSourceConfig config) {
        if (config == null || !config.isConfigOk()) {
            return;
        }

        datasourceConfigs.put(config.getName(), config);
    }


    public Map<String, DataSourceConfig> getDatasourceConfigs() {
        return datasourceConfigs;
    }

    public DataSourceConfig getMainDatasourceConfig() {
        return datasourceConfigs.get(DataSourceConfig.NAME_DEFAULT);
    }
}
