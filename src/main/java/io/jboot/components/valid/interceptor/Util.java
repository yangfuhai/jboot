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
package io.jboot.components.valid.interceptor;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

class Util {

    public static long getObjectLen(Object validObject) {
        if (validObject == null) {
            return 0;
        }
        if (validObject instanceof Number) {
            return ((Number) validObject).longValue();
        }
        if (validObject instanceof CharSequence) {
            return ((CharSequence) validObject).length();
        }
        if (validObject instanceof Map) {
            return ((Map<?, ?>) validObject).size();
        }
        if (validObject instanceof Collection) {
            return ((Collection) validObject).size();
        }
        if (validObject.getClass().isArray()) {
            return Array.getLength(validObject);
        }
        throw new IllegalArgumentException("Can not get object length for class: " + validObject.getClass().getName());
    }

}
