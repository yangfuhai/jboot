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
package io.jboot.db.datasource;

import com.jfinal.kit.Prop;
import io.jboot.config.JbootProperties;
import io.jboot.utils.StringUtils;

import java.util.*;

public class DatasourceConfigManager {


    private static DatasourceConfigManager manager = new DatasourceConfigManager();

    public static DatasourceConfigManager me() {
        return manager;
    }

    private List<DatasourceConfig> datasourceConfigs = new ArrayList<>();

    private DatasourceConfigManager() {

        DatasourceConfig datasourceConfig = JbootProperties.get("jboot.datasource", DatasourceConfig.class);
        if (datasourceConfig.isConfigOk()) {
            datasourceConfigs.add(datasourceConfig);
        }


        Prop prop = JbootProperties.getJbootProp();
        Set<String> datasourceNames = new HashSet<>();
        for (Map.Entry<Object, Object> entry : prop.getProperties().entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith("jboot.datasource.") && entry.getValue() != null) {
                String[] keySplits = key.split(".");
                if (keySplits.length == 4) {
                    datasourceNames.add(keySplits[2]);
                }
            }
        }


        for (String name : datasourceNames) {
            DatasourceConfig dsc = JbootProperties.get("jboot.datasource." + name, DatasourceConfig.class);
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
