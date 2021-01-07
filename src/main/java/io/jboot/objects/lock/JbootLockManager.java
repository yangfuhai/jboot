/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.objects.lock;

import io.jboot.Jboot;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.objects.counter.JbootCounterConfig;
import io.jboot.objects.lock.impl.JbootLocalLock;
import io.jboot.objects.lock.impl.JbootRedisLock;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/2/28
 */
public class JbootLockManager {

    private static JbootLockManager instance = new JbootLockManager();
    public static JbootLockManager me() {
        return instance;
    }


    private JbootLockConfig config = Jboot.config(JbootLockConfig.class);

    public JbootLock create(String name){
        switch (config.getType()){
            case JbootCounterConfig.TYPE_LOCAL:
                return new JbootLocalLock(name);
            case JbootCounterConfig.TYPE_REDIS:
                return new JbootRedisLock(name);
            default:
                return JbootSpiLoader.load(JbootLock.class,config.getType());
        }


    }

}
