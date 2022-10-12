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

import com.jfinal.core.Controller;
import io.jboot.web.controller.JbootController;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author michael yang (fuhai999@gmail.com)
 * <p>
 * InterceptorBuilder 用于控制某个方法已经添加好的拦截器，可以对其删除或者添加
 *
 * <pre>
 * 配置方法：
 * public void onInit() {
 *     InterceptorBuilderManager.me().addInterceptorBuilder(new MyInterceptorBuilder());
 * }
 *
 * 或者给 MyInterceptorBuilder 类添加 @AutoLoad 注解
 * </pre>
 */
public interface InterceptorBuilder {

    void build(Class<?> targetClass, Method method, Interceptors interceptors);

    class Util {

        public static boolean isChildClassOf(Class<?> childClass, Class<?> parentClass) {
            return parentClass.isAssignableFrom(childClass);
        }

        public static boolean isController(Class<?> serviceClass) {
            return Controller.class.isAssignableFrom(serviceClass);
        }

        public static boolean isJbootController(Class<?> serviceClass) {
            return JbootController.class.isAssignableFrom(serviceClass);
        }

        public static <A extends Annotation> boolean hasAnnotation(Class<?> targetClass, Class<A> annotationClass) {
            return targetClass.getAnnotation(annotationClass) != null;
        }


        public static <A extends Annotation> boolean hasAnnotation(Method method, Class<A> annotationClass) {
            return method.getAnnotation(annotationClass) != null;
        }


        public static boolean hasAnyAnnotation(Class<?> targetClass, Class<? extends Annotation>... annotationClass) {
            for (Class<? extends Annotation> clazz : annotationClass) {
                if (hasAnnotation(targetClass, clazz)) {
                    return true;
                }
            }
            return false;
        }


        public static boolean hasAnyAnnotation(Method method, Class<? extends Annotation>... annotationClass) {
            for (Class<? extends Annotation> clazz : annotationClass) {
                if (hasAnnotation(method, clazz)) {
                    return true;
                }
            }
            return false;
        }


        public static Annotation getAnyAnnotation(Class<?> targetClass, Class<? extends Annotation>... annotationClass) {
            for (Class<? extends Annotation> clazz : annotationClass) {
                Annotation a = targetClass.getAnnotation(clazz);
                if (a != null) {
                    return a;
                }
            }
            return null;
        }


        public static Annotation getAnyAnnotation(Method method, Class<? extends Annotation>... annotationClass) {
            for (Class<? extends Annotation> clazz : annotationClass) {
                Annotation a = method.getAnnotation(clazz);
                if (a != null) {
                    return a;
                }
            }
            return null;
        }


        public static <A extends Annotation> boolean hasAnnotation(Class<?> targetClass, Method method, Class<A> annotationClass) {
            return hasAnnotation(targetClass, annotationClass) || hasAnnotation(method, annotationClass);
        }
    }
}
