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
package io.jboot.core.cache.ehcache;

import com.jfinal.log.Log;
import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.core.cache.JbootCacheBase;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.util.List;


public class JbootEhcacheImpl extends JbootCacheBase {

    private static CacheManager cacheManager;
    private static Object locker = new Object();

    private static final Log log = Log.getLog(JbootEhcacheImpl.class);

    public JbootEhcacheImpl() {
        cacheManager = CacheManager.create();
    }

    public static Cache getOrAddCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            synchronized (locker) {
                cache = cacheManager.getCache(cacheName);
                if (cache == null) {
                    log.warn("Could not find cache config [" + cacheName + "], using default.");
                    cacheManager.addCacheIfAbsent(cacheName);
                    cache = cacheManager.getCache(cacheName);
                }
            }
        }
        return cache;
    }

    @Override
    public List getKeys(String cacheName) {
        return getOrAddCache(cacheName).getKeys();
    }

    @Override
    public <T> T get(String cacheName, Object key) {
        Element element = getOrAddCache(cacheName).get(key);
        return element != null ? (T) element.getObjectValue() : null;
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        getOrAddCache(cacheName).put(new Element(key, value));
    }

    @Override
    public void put(String cacheName, Object key, Object value, int liveSeconds) {
        Element element = new Element(key, value);
        element.setTimeToLive(liveSeconds);
        getOrAddCache(cacheName).put(element);
    }

    @Override
    public void remove(String cacheName, Object key) {
        getOrAddCache(cacheName).remove(key);
    }

    @Override
    public void removeAll(String cacheName) {
        getOrAddCache(cacheName).removeAll();
    }

    @Override
    public <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
        Object data = get(cacheName, key);
        if (data == null) {
            data = dataLoader.load();
            put(cacheName, key, data);
        }
        return (T) data;
    }
}
