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
package io.jboot.aop;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.InterceptorManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class Interceptors {

    private List<InterceptorWarpper> warppers = new ArrayList<>();

    private int minimalWeight = 1;
    private int maximumWeight = 1;
    private int currentWeight = 1;

    public Interceptors() {
    }

    public Interceptors(Interceptor[] inters) {
        if (inters != null && inters.length > 0) {
            for (Interceptor interceptor : inters) {
                add(interceptor);
            }
        }
    }


    public void add(Interceptor interceptor) {
        warppers.add(new InterceptorWarpper(interceptor, currentWeight++));

        if (currentWeight > maximumWeight) {
            maximumWeight = currentWeight;
        }
    }


    public void add(Interceptor interceptor, int weight) {
        warppers.add(new InterceptorWarpper(interceptor, weight));

        if (weight >= maximumWeight) {
            maximumWeight = weight + 1;
        }
        if (weight <= minimalWeight) {
            minimalWeight = weight - 1;
        }
    }


    public void addToFirst(Interceptor interceptor) {
        warppers.add(new InterceptorWarpper(interceptor, --minimalWeight));
    }


    public void addToLast(Interceptor interceptor) {
        warppers.add(new InterceptorWarpper(interceptor, ++maximumWeight));
    }


    public void addBefore(Interceptor interceptor, Class<? extends Interceptor> interceptroClass) {
        int weight = ++currentWeight;
        for (InterceptorWarpper warpper : warppers) {
            if (warpper.getInterceptor().getClass() == interceptroClass) {
                weight = warpper.weight - 1;
            }
        }
        warppers.add(new InterceptorWarpper(interceptor, weight));
        if (currentWeight > maximumWeight) {
            maximumWeight = currentWeight;
        }
    }


    public void addAfter(Interceptor interceptor, Class<? extends Interceptor> interceptroClass) {
        int weight = ++currentWeight;
        for (InterceptorWarpper warpper : warppers) {
            if (warpper.getInterceptor().getClass() == interceptroClass) {
                weight = warpper.weight + 1;
            }
        }
        warppers.add(new InterceptorWarpper(interceptor, weight));

        if (currentWeight > maximumWeight) {
            maximumWeight = currentWeight;
        }
    }


    public void remove(Interceptor interceptor) {
        warppers.removeIf(interceptorWarpper -> interceptorWarpper.interceptor == interceptor);
    }


    public void removeByClass(Class clazz) {
        warppers.removeIf(interceptorWarpper -> interceptorWarpper.interceptor.getClass() == clazz);
    }


    public Integer getWeight(Interceptor interceptor) {
        for (InterceptorWarpper warpper : warppers) {
            if (warpper.interceptor == interceptor) {
                return warpper.weight;
            }
        }
        return null;
    }


    public Integer getWeightByClass(Class<? extends Interceptor> clazz) {
        for (InterceptorWarpper warpper : warppers) {
            if (warpper.interceptor.getClass() == clazz) {
                return warpper.weight;
            }
        }
        return null;
    }


    public List<Interceptor> toList() {
        warppers.sort(Comparator.comparingInt(InterceptorWarpper::getWeight));
        return warppers.stream().map(InterceptorWarpper::getInterceptor).collect(Collectors.toList());
    }


    public Interceptor[] toArray() {
        if (warppers == null || warppers.size() == 0) {
            return InterceptorManager.NULL_INTERS;
        } else {
            warppers.sort(Comparator.comparingInt(InterceptorWarpper::getWeight));
            Interceptor[] inters = new Interceptor[warppers.size()];
            for (int i = 0; i < warppers.size(); i++) {
                inters[i] = warppers.get(i).getInterceptor();
            }
            return inters;
        }
    }

    public int getMinimalWeight() {
        return minimalWeight;
    }

    public int getMaximumWeight() {
        return maximumWeight;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }


    static class InterceptorWarpper {

        private Interceptor interceptor;
        private int weight;

        public InterceptorWarpper(Interceptor interceptor, int weight) {
            this.interceptor = interceptor;
            this.weight = weight;
        }

        public Interceptor getInterceptor() {
            return interceptor;
        }

        public void setInterceptor(Interceptor interceptor) {
            this.interceptor = interceptor;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }

}
