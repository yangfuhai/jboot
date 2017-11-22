/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.db.datasource;

import io.jboot.Jboot;
import io.jboot.config.JbootConfigManager;
import io.jboot.utils.StringUtils;

import java.util.*;

public class DatasourceConfigManager {


    private static DatasourceConfigManager manager = new DatasourceConfigManager();

    public static DatasourceConfigManager me() {
        return manager;
    }

    private List<DatasourceConfig> datasourceConfigs = new ArrayList<>();

    private DatasourceConfigManager() {

        DatasourceConfig datasourceConfig = Jboot.config(DatasourceConfig.class, "jboot.datasource");
        if (datasourceConfig.isConfigOk()) {
            datasourceConfigs.add(datasourceConfig);
        }


        Properties prop = JbootConfigManager.me().getProperties();
        Set<String> datasourceNames = new HashSet<>();
        for (Map.Entry<Object, Object> entry : prop.entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith("jboot.datasource.") && entry.getValue() != null) {
                String[] keySplits = key.split("\\.");
                if (keySplits.length == 4) {
                    datasourceNames.add(keySplits[2]);
                }
            }
        }


        for (String name : datasourceNames) {
            DatasourceConfig dsc = Jboot.config(DatasourceConfig.class, "jboot.datasource." + name);
            if (StringUtils.isBlank(dsc.getName())) {
                dsc.setName(name);
            }
            if (dsc.isConfigOk()) {
                datasourceConfigs.add(dsc);
            }
        }
    }


    public List<DatasourceConfig> getDatasourceConfigs() {
        return datasourceConfigs;
    }

}
