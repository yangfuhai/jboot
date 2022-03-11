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

import com.jfinal.plugin.activerecord.Config;
import io.jboot.db.dialect.JbootDialect;

public class CPI {

    public static boolean hasAnyJoinEffective(JbootModel dao) {
        return dao.hasAnyJoinEffective();
    }


    public static boolean hasColumn(JbootModel dao, String columnLabel) {
        return dao._hasColumn(columnLabel);
    }


    public static JbootDialect getJbootDialect(JbootModel dao) {
        return dao._getDialect();
    }


    public static Config getModelConfig(JbootModel dao) {
        return dao._getConfig();
    }


    public static <M> M loadByCache(JbootModel dao, Object... idValues) {
        return (M) dao.loadByCache(idValues);
    }


    public static void safeDeleteCache(JbootModel dao, Object... idValues) {
        dao.safeDeleteCache(idValues);
    }


    public static Class<?> safeDeleteCache(JbootModel dao) {
        return dao._getPrimaryType();
    }


    public static String buildIdCacheName(JbootModel dao, String name) {
        return dao.buildIdCacheName(name);
    }


    public static String buildIdCacheKey(JbootModel dao, Object... idValues) {
        return dao.buildIdCacheKey(idValues);
    }


}
