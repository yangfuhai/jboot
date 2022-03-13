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
package io.jboot.aop;

import com.jfinal.aop.Interceptor;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.core.weight.WeightUtil;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.ClassUtil;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public class InterceptorBuilderManager{


    private static InterceptorBuilderManager me = new InterceptorBuilderManager();


    public static InterceptorBuilderManager me() {
        return me;
    }

    public InterceptorBuilderManager() {
        List<Class<InterceptorBuilder>> builderClasses = ClassScanner.scanSubClass(InterceptorBuilder.class,true);
        if (builderClasses != null){
            for (Class<InterceptorBuilder> builderClass : builderClasses){
                if (builderClass.getAnnotation(AutoLoad.class) != null){
                    addInterceptorBuilder(ClassUtil.newInstance(builderClass,false));
                }
            }

            InterceptorCache.clear();
        }
    }

    private List<InterceptorBuilder> interceptorBuilders = new CopyOnWriteArrayList();


    public List<InterceptorBuilder> getInterceptorBuilders() {
        return interceptorBuilders;
    }



    public void addInterceptorBuilder(InterceptorBuilder interceptorBuilder) {
        if (interceptorBuilder == null) {
            throw new NullPointerException("interceptorBuilder must not be null.");
        }
        this.interceptorBuilders.add(interceptorBuilder);
        WeightUtil.sort(this.interceptorBuilders);

        InterceptorCache.clear();
    }




    public void addInterceptorBuilder(Collection<InterceptorBuilder> interceptorBuilders) {
        if (interceptorBuilders == null) {
            throw new NullPointerException("interceptorBuilder must not be null.");
        }
        this.interceptorBuilders.addAll(interceptorBuilders);
        WeightUtil.sort(this.interceptorBuilders);

        InterceptorCache.clear();
    }



    public void removeInterceptorBuilder(Predicate<? super InterceptorBuilder> filter){
        if (interceptorBuilders != null && !interceptorBuilders.isEmpty()){
            if (interceptorBuilders.removeIf(filter)) {
                InterceptorCache.clear();
            }
        }
    }



    public Interceptor[] build(Class<?> targetClass, Method method, Interceptor[] inters) {
        if (interceptorBuilders != null && interceptorBuilders.size() > 0) {
            Interceptors interceptors = new Interceptors(inters);
            for (InterceptorBuilder builder : interceptorBuilders) {
                builder.build(targetClass, method, interceptors);
            }
            return interceptors.toArray();
        }
        return inters;
    }



}
