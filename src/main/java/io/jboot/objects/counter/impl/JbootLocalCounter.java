/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.objects.counter.impl;

import io.jboot.objects.counter.JbootCounter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/7
 */
public class JbootLocalCounter implements JbootCounter {

    private static Map<String, AtomicLong> atomicLongs = new HashMap<>();

    private AtomicLong atomicLong;

    public JbootLocalCounter(String name) {
        atomicLong = atomicLongs.get(name);
        if (atomicLong == null) {
            synchronized (JbootLocalCounter.class) {
                atomicLong = atomicLongs.get(name);
                if (atomicLong == null) {
                    atomicLong = new AtomicLong();
                    atomicLongs.put(name, atomicLong);
                }
            }
        }
    }

    @Override
    public Long increment() {
        return atomicLong.incrementAndGet();
    }

    @Override
    public Long decrement() {
        return atomicLong.decrementAndGet();
    }

    @Override
    public Long get() {
        return atomicLong.get();
    }

    @Override
    public void set(long newValue) {
        atomicLong.set(newValue);
    }
}
