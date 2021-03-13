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
package io.jboot.aop;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.InterceptorManager;
import io.jboot.utils.ClassUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class Interceptors {

    private List<InterceptorWarpper> warppers = new ArrayList<>();

    private int minimalWeight = 1;
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
    }

    public void add(Class<? extends Interceptor> interceptorClass) {
        add(singleton(interceptorClass));
    }

    public void add(Interceptor interceptor, int weight) {
        warppers.add(new InterceptorWarpper(interceptor, weight));
    }

    public void add(Class<? extends Interceptor> interceptorClass, int weight) {
        add(singleton(interceptorClass), weight);
    }


    public void addIfNotExist(Interceptor interceptor) {
        if (!hasInterceptor(interceptor)) {
            add(interceptor);
        }
    }

    public void addIfNotExist(Class<? extends Interceptor> interceptorClass) {
        if (!hasInterceptor(interceptorClass)) {
            add(singleton(interceptorClass));
        }
    }


    public void addToFirst(Interceptor interceptor) {
        warppers.add(new InterceptorWarpper(interceptor, --minimalWeight));
    }

    public void addToFirst(Class<? extends Interceptor> interceptorClass) {
        addToFirst(singleton(interceptorClass));
    }

    public boolean addBefore(Interceptor interceptor, Predicate<? super Interceptor> filter) {
        Integer weight = null;
        for (InterceptorWarpper warpper : warppers) {
            if (filter.test(warpper.interceptor)) {
                weight = warpper.weight;
                break;
            }
        }

        //所有在 新加 的拦截器往前推 1
        if (weight != null) {
            for (InterceptorWarpper warpper : warppers) {
                if (warpper.weight < weight) {
                    warpper.weight--;
                }
            }
            minimalWeight--;
            warppers.add(new InterceptorWarpper(interceptor, --weight));
            return true;
        } else {
            return false;
        }
    }

    public boolean addBefore(Class<? extends Interceptor> interceptorClass, Predicate<? super Interceptor> filter) {
        return addBefore(singleton(interceptorClass), filter);
    }

    public boolean addBefore(Interceptor interceptor, Class<? extends Interceptor> toClass) {
        return addBefore(interceptor, interceptor1 -> interceptor1.getClass() == toClass);
    }

    public boolean addBefore(Class<? extends Interceptor> interceptorClass, Class<? extends Interceptor> toClass) {
        return addBefore(singleton(interceptorClass), toClass);
    }


    public boolean addAfter(Interceptor interceptor, Predicate<? super Interceptor> filter) {
        Integer weight = null;
        for (InterceptorWarpper warpper : warppers) {
            if (filter.test(warpper.interceptor)) {
                weight = warpper.weight;
                break;
            }
        }

        //所有在 新加 的拦截器往后推 1
        if (weight != null) {
            for (InterceptorWarpper warpper : warppers) {
                if (warpper.weight > weight) {
                    warpper.weight++;
                }
            }
            currentWeight++;
            warppers.add(new InterceptorWarpper(interceptor, ++weight));
            return true;
        } else {
            return false;
        }
    }

    public boolean addAfter(Class<? extends Interceptor> interceptorClass, Predicate<? super Interceptor> filter) {
        return addAfter(singleton(interceptorClass), filter);
    }


    public boolean addAfter(Interceptor interceptor, Class<? extends Interceptor> toClass) {
        return addAfter(interceptor, interceptor1 -> interceptor1.getClass() == toClass);
    }


    public boolean addAfter(Class<? extends Interceptor> interceptorClass, Class<? extends Interceptor> toClass) {
        return addAfter(singleton(interceptorClass), toClass);
    }


    public boolean remove(Interceptor interceptor) {
        return warppers.removeIf(interceptorWarpper -> interceptorWarpper.interceptor == interceptor);
    }


    public boolean remove(Predicate<? super Interceptor> predicate) {
        return warppers.removeIf(warpper -> predicate.test(warpper.interceptor));
    }


    public boolean remove(Class<? extends Interceptor> clazz) {
        return warppers.removeIf(interceptorWarpper -> interceptorWarpper.interceptor.getClass() == clazz);
    }


    public Integer getWeight(Interceptor interceptor) {
        for (InterceptorWarpper warpper : warppers) {
            if (warpper.interceptor == interceptor) {
                return warpper.weight;
            }
        }
        return null;
    }


    public Integer getWeight(Class<? extends Interceptor> clazz) {
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


    public boolean hasInterceptor(Interceptor interceptor) {
        for (InterceptorWarpper warpper : warppers) {
            if (warpper.interceptor == interceptor) {
                return true;
            }
        }
        return false;
    }


    public boolean hasInterceptor(Class<? extends Interceptor> clazz) {
        for (InterceptorWarpper warpper : warppers) {
            if (warpper.interceptor.getClass() == clazz) {
                return true;
            }
        }
        return false;
    }


    public int getMinimalWeight() {
        return minimalWeight;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }


    private Interceptor singleton(Class<? extends Interceptor> interceptorClass) {
        return ClassUtil.singleton(interceptorClass, false, true);
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
