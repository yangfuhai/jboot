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
package io.jboot.cache.ehcache;

import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import io.jboot.cache.JbootCacheBase;

import java.util.List;


public class JbootEhcacheImpl extends JbootCacheBase {

    @Override
    public List getKeys(String cacheName) {
        return CacheKit.getKeys(cacheName);
    }

    @Override
    public <T> T get(String cacheName, Object key) {
        return CacheKit.get(cacheName, key);
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        CacheKit.put(cacheName, key, value);
    }

    @Override
    public void remove(String cacheName, Object key) {
        CacheKit.remove(cacheName, key);
    }

    @Override
    public void removeAll(String cacheName) {
        CacheKit.removeAll(cacheName);
    }

    @Override
    public <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
        return CacheKit.get(cacheName, key, dataLoader);
    }
}
