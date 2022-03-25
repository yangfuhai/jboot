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
package io.jboot.db;

import com.jfinal.plugin.activerecord.Model;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.utils.StrUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class TableInfo {

    private String tableName;
    private String primaryKey;
    private Class<? extends Model<?>> modelClass;
    private String datasource;
    private Set<String> datasourceNames;

    private List<DataSourceConfigWrapper> attachedDatasources;


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Class<? extends Model<?>> getModelClass() {
        return modelClass;
    }

    public void setModelClass(Class<? extends Model<?>> modelClass) {
        this.modelClass = modelClass;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public Set<String> getDatasourceNames() {
        if (datasourceNames == null) {
            datasourceNames = StrUtil.isNotBlank(datasource)
                    ? StrUtil.splitToSetByComma(datasource)
                    : new HashSet<>();
        }
        return datasourceNames;
    }


    /**
     * 添加数据源：让此表绑定数据源
     *
     * @param dataSourceConfig
     * @param fromDesignated   是否是通过 jboot.datasource.table 或者 @table(datasource="xxx") 来指定的
     */
    public boolean addAttachedDatasource(DataSourceConfig dataSourceConfig, boolean fromDesignated) {
        if (this.attachedDatasources == null) {
            this.attachedDatasources = new ArrayList<>();
        }

        // 若当前表未指定数据源 （fromDesignated == false），且当前表已经存在了指定数据源的 datasource
        // 则不能再添加该数据源
        if (!fromDesignated && !this.attachedDatasources.isEmpty()) {
            for (DataSourceConfigWrapper dataSourceConfigWrapper : this.attachedDatasources) {
                if (dataSourceConfigWrapper.fromDesignated) {
                    return false;
                }
            }
        }

        this.attachedDatasources.add(new DataSourceConfigWrapper(dataSourceConfig, fromDesignated));

        // 若新添加的数据源，是配置了指定的表（亦或者表配置了指定的数据源），那么需要移除哪些未指定的默认数据源
        if (fromDesignated) {
            for (DataSourceConfigWrapper dataSourceConfigWrapper : attachedDatasources) {
                if (!dataSourceConfigWrapper.fromDesignated) {
                    dataSourceConfigWrapper.dataSourceConfig.removeTableInfo(this);
                }
            }
            attachedDatasources.removeIf(dataSourceConfigWrapper -> !dataSourceConfigWrapper.fromDesignated);
        }

        return true;
    }


    public static class DataSourceConfigWrapper {
        private final DataSourceConfig dataSourceConfig;
        private final boolean fromDesignated;

        public DataSourceConfigWrapper(DataSourceConfig dataSourceConfig, boolean fromDesignated) {
            this.dataSourceConfig = dataSourceConfig;
            this.fromDesignated = fromDesignated;
        }
    }

}
