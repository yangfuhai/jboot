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
package io.jboot.components.cache;


import io.jboot.app.config.JbootConfigManager;
import io.jboot.app.config.annotation.ConfigModel;


@ConfigModel(prefix = "jboot.aop.cache")
public class AopCacheConfig {

    // AOP 缓存的默认有效时间，0 为永久有效，单位秒，
    // 当 @Cacheable 和 @CachePut 注解不配置的时候默认用这个配置

    private int liveSeconds = 60 * 10; //默认为 10 分钟
    private String useCacheName = "default";

    public int getLiveSeconds() {
        return liveSeconds;
    }

    public void setLiveSeconds(int liveSeconds) {
        this.liveSeconds = liveSeconds;
    }

    public String getUseCacheName() {
        return useCacheName;
    }

    public void setUseCacheName(String useCacheName) {
        this.useCacheName = useCacheName;
    }


    private static AopCacheConfig me;

    public static AopCacheConfig getInstance() {
        if (me == null) {
            me = JbootConfigManager.me().get(AopCacheConfig.class);
        }
        return me;
    }

}
