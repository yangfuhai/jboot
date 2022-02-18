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
package io.jboot.db.model;

import io.jboot.Jboot;
import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.components.cache.JbootCache;
import io.jboot.components.cache.JbootCacheManager;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@ConfigModel(prefix = "jboot.model")
public class JbootModelConfig {

    private String scanPackage;
    private String unscanPackage;

    private String columnCreated = "created";
    private String columnModified = "modified";

    /**
     * id 缓存的时间，默认为 1 个小时，单位：秒
     */
    private int idCacheTime = 60 * 60;

    /**
     * Model 过滤器，可以通过这个配置来防止 xss 等问题
     * filter 会在 save 和 update 的时候被执行
     */
    private String filterClass;

    /**
     * 主键的值的生成器，可以通过配置这个来自定义主键的生成策略
     */
    private String primarykeyValueGeneratorClass;


    /**
     * 是否启用 id 缓存，如果启用，当根据 id 查询的时候，会自动存入缓存
     * 下次再通过 id 查询的时候，直接从缓存中获取 Model
     */
    private boolean idCacheEnable = true;

    /**
     * 从缓存获取数据的时候，是复制一个返回，这样保证前端在修改的时候不修改到缓存数据
     */
    private boolean idCacheByCopyEnable = true;


    private String idCacheName = "default";


    public JbootModelConfig() {
    }

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    public String getUnscanPackage() {
        return unscanPackage;
    }

    public void setUnscanPackage(String unscanPackage) {
        this.unscanPackage = unscanPackage;
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

    public String getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(String filterClass) {
        this.filterClass = filterClass;
    }

    public String getPrimarykeyValueGeneratorClass() {
        return primarykeyValueGeneratorClass;
    }

    public void setPrimarykeyValueGeneratorClass(String primarykeyValueGeneratorClass) {
        this.primarykeyValueGeneratorClass = primarykeyValueGeneratorClass;
    }

    public boolean isIdCacheEnable() {
        return idCacheEnable;
    }

    public void setIdCacheEnable(boolean idCacheEnable) {
        this.idCacheEnable = idCacheEnable;
    }

    public boolean isIdCacheByCopyEnable() {
        return idCacheByCopyEnable;
    }

    public void setIdCacheByCopyEnable(boolean idCacheByCopyEnable) {
        this.idCacheByCopyEnable = idCacheByCopyEnable;
    }


    public JbootModelConfig(String idCacheName) {
        this.idCacheName = idCacheName;
    }

    private JbootModelFilter filter;

    public JbootModelFilter getFilter() {
        if (filter == null) {
            if (StrUtil.isNotBlank(filterClass)) {
                filter = ClassUtil.newInstance(filterClass);
            } else {
                filter = JbootModelFilter.DEFAULT;
            }
        }
        return filter;
    }

    public void setFilter(JbootModelFilter filter) {
        this.filter = filter;
    }


    private PrimarykeyValueGenerator primarykeyValueGenerator;

    public PrimarykeyValueGenerator getPrimarykeyValueGenerator() {
        if (primarykeyValueGenerator == null) {
            if (StrUtil.isNotBlank(primarykeyValueGeneratorClass)) {
                primarykeyValueGenerator = ClassUtil.newInstance(primarykeyValueGeneratorClass);
            } else {
                primarykeyValueGenerator = PrimarykeyValueGenerator.DEFAULT;
            }
        }
        return primarykeyValueGenerator;
    }


    public void setPrimarykeyValueGenerator(PrimarykeyValueGenerator primarykeyValueGenerator) {
        this.primarykeyValueGenerator = primarykeyValueGenerator;
    }


    private static JbootModelConfig config;

    public static JbootModelConfig getConfig() {
        if (config == null) {
            config = Jboot.config(JbootModelConfig.class);
        }
        return config;
    }

    private JbootCache idCache;

    public JbootCache getIdCache() {
        if (idCache == null) {
            idCache = JbootCacheManager.me().getCache(idCacheName);
        }
        return idCache;
    }

    public void setIdCache(JbootCache idCache) {
        this.idCache = idCache;
    }


}
