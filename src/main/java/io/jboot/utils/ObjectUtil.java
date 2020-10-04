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
package io.jboot.utils;

import java.util.Collection;
import java.util.Objects;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class ObjectUtil {


    /**
     * 判断 某个 objects 集合里是否包含了某个 object
     *
     * @param objects            object 集合
     * @param compareObject      是否被包 集合 含的对比 object
     * @param compareAttrGetters 需要对比的 getter
     * @param <T>
     * @return true 包含，false 不包含
     */
    public static <T> boolean isContainsObject(Collection<T> objects, T compareObject, ObjectFunc<T>... compareAttrGetters) {
        if (objects == null || objects.isEmpty() || compareObject == null) {
            return false;
        }

        if (compareAttrGetters == null || compareAttrGetters.length == 0) {
            throw new IllegalArgumentException("compareAttrGetters must not be null");
        }


        for (T object : objects) {
            if (isSameObject(object, compareObject, compareAttrGetters)) {
                return true;
            }
        }

        return false;
    }


    /**
     * 获取 某个 objects 集合里包含的 object
     *
     * @param objects            object 集合
     * @param compareObject      是否被包 集合 含的对比 object
     * @param compareAttrGetters 需要对比的 getter
     * @param <T>
     * @return 返回 objects 结合中对比成功的 object
     */
    public static <T> T getContainsObject(Collection<T> objects, T compareObject, ObjectFunc<T>... compareAttrGetters) {
        if (objects == null || objects.isEmpty() || compareObject == null) {
            return null;
        }

        if (compareAttrGetters == null || compareAttrGetters.length == 0) {
            throw new IllegalArgumentException("compareAttrGetters must not be null");
        }


        for (T object : objects) {
            if (isSameObject(object, compareObject, compareAttrGetters)) {
                return object;
            }
        }

        return null;
    }


    /**
     * 判断两个 Object 是否是同一个 Object，根据传入的 getter 来进行对比
     *
     * @param object1
     * @param object2
     * @param compareAttrGetters
     * @param <T>
     * @return
     */
    public static <T> boolean isSameObject(T object1, T object2, ObjectFunc<T>... compareAttrGetters) {
        if (object1 == null || object2 == null) {
            return object1 == object2;
        }

        if (compareAttrGetters == null || compareAttrGetters.length == 0) {
            throw new IllegalArgumentException("compareAttrGetters must not be null");
        }


        for (ObjectFunc getter : compareAttrGetters) {

            if (getter == null) {
                throw new IllegalArgumentException("compareAttrGetter must not be null");
            }


            if (!Objects.equals(getter.get(object1), getter.get(object2))) {
                return false;
            }
        }

        return true;
    }


    /**
     * 判断两个 Object 是否是同一个 Object，根据传入的 getter 来进行对比
     *
     * @param object1
     * @param object2
     * @param compareAttrGetters
     * @param <T>
     * @return
     */
    public static <T> boolean notSameObject(T object1, T object2, ObjectFunc<T>... compareAttrGetters) {
        return !isSameObject(object1, object2, compareAttrGetters);
    }

}
