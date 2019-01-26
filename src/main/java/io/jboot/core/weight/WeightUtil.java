/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.core.weight;


import java.util.List;

public class WeightUtil {

    public static void sort(List list) {
        if (list == null && list.isEmpty()) {
            return;
        }
        list.sort((o1, o2) -> {
            Weight sort1 = o1.getClass().getAnnotation(Weight.class);
            Weight sort2 = o2.getClass().getAnnotation(Weight.class);
            int value1 = sort1 == null ? 0 : sort1.value();
            int value2 = sort2 == null ? 0 : sort2.value();
            return value1 - value2;
        });
    }
}
