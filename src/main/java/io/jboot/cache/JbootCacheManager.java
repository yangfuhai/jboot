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
package io.jboot.cache;

import io.jboot.Jboot;
import io.jboot.cache.ehcache.JbootEhcacheImpl;
import io.jboot.cache.ehredis.JbootEhredisCacheImpl;
import io.jboot.cache.redis.JbootRedisCacheImpl;


public class JbootCacheManager {

    private static JbootCacheManager me = new JbootCacheManager();

    private JbootCacheManager() {
    }

    private JbootCache jbootCache;

    public static JbootCacheManager me() {
        return me;
    }

    public JbootCache getCache() {
        if (jbootCache == null) {
            initCache();
        }
        return jbootCache;
    }

    private void initCache() {
        JbootCacheConfig config = Jboot.config(JbootCacheConfig.class);
        switch (config.getType()) {
            case JbootCacheConfig.TYPE_EHCACHE:
                jbootCache = new JbootEhcacheImpl();
                break;
            case JbootCacheConfig.TYPE_REDIS:
                jbootCache = new JbootRedisCacheImpl();
                break;
            case JbootCacheConfig.TYPE_EHREDIS:
                jbootCache = new JbootEhredisCacheImpl();
                break;
            case JbootCacheConfig.TYPE_NO_CACHE:
                jbootCache = new NoCacheImpl();
        }
    }
}
