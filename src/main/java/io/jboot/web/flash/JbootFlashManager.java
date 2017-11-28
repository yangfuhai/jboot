/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.flash;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.jboot.web.JbootControllerContext;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.flash
 */
public class JbootFlashManager {

    Cache<String, Cache<String, Object>> flashes = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    private static JbootFlashManager me = new JbootFlashManager();

    public static JbootFlashManager me() {
        return me;
    }

    public void addFlash(String key, Object value) throws ExecutionException {
        if (value == null) {
            throw new NullPointerException("flash value must not be null");
        }

        String sessionId = JbootControllerContext.get().getSession().getId();
        Cache<String, Object> flash = flashes.get(sessionId, new Callable<Cache<String, Object>>() {
            @Override
            public Cache<String, Object> call() throws Exception {
                return CacheBuilder.newBuilder()
                        .expireAfterAccess(2, TimeUnit.MINUTES)
                        .build();
            }
        });
        flash.put(key, value);
    }


    public void clearFlash() {
        String sessionId = JbootControllerContext.get().getSession().getId();
        Cache<String, Object> flash = flashes.getIfPresent(sessionId);
        if (flash != null) {
            flash.invalidateAll();
        }
    }


    public <T> T getFlash(String key) {
        String sessionId = JbootControllerContext.get().getSession().getId();
        Cache<String, Object> flash = flashes.getIfPresent(sessionId);
        return flash == null ? null : (T) flash.getIfPresent(key);
    }


    public Map<String, Object> getFlashes() {
        String sessionId = JbootControllerContext.get().getSession().getId();
        Cache<String, Object> flash = flashes.getIfPresent(sessionId);
        return flash == null ? null : flash.asMap();
    }

}
