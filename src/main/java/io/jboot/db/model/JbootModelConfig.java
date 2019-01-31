/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.db.model;

import io.jboot.Jboot;
import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.components.cache.JbootCache;
import io.jboot.components.cache.JbootCacheConfig;
import io.jboot.components.cache.JbootCacheManager;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.db.model
 */
@ConfigModel(prefix = "jboot.model")
public class JbootModelConfig {

    private String scan;

    private String columnCreated = "created";
    private String columnModified = "modified";
    private int idCacheTime = 60 * 60 * 1; // id 缓存默认缓存1个小时
    private boolean idCacheEnable = true; // 是否启用ID自动缓存
    private String idCacheType = Jboot.config(JbootCacheConfig.class).getType();


    public String getScan() {
        return scan;
    }

    public void setScan(String scan) {
        this.scan = scan;
    }

    public String getColumnCreated() {
        return columnCreated;
    }

    public void setColumnCreated(String columnCreated) {
        this.columnCreated = columnCreated;
    }

    public String getColumnModified() {
        return columnModified;
    }

    public void setColumnModified(String columnModified) {
        this.columnModified = columnModified;
    }

    public int getIdCacheTime() {
        return idCacheTime;
    }

    public void setIdCacheTime(int idCacheTime) {
        this.idCacheTime = idCacheTime;
    }

    public boolean isIdCacheEnable() {
        return idCacheEnable;
    }

    public void setIdCacheEnable(boolean idCacheEnable) {
        this.idCacheEnable = idCacheEnable;
    }

    public String getIdCacheType() {
        return idCacheType;
    }

    public void setIdCacheType(String idCacheType) {
        this.idCacheType = idCacheType;
    }

    private static JbootModelConfig config;

    public static JbootModelConfig getConfig() {
        if (config == null) {
            config = Jboot.config(JbootModelConfig.class);
        }
        return config;
    }

    private JbootCache jbootCache;

    public JbootCache getCache() {
        if (jbootCache == null) {
            jbootCache = JbootCacheManager.me().getCache(idCacheType);
        }
        return jbootCache;
    }
}
