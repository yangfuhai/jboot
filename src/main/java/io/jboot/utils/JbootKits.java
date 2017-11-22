/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.utils;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.utils
 */
public class JbootKits {

    public static Class<?> getUsefulClass(Class<?> clazz) {
        //ControllerTest$ServiceTest$$EnhancerByGuice$$40471411#hello
        //com.demo.blog.Blog$$EnhancerByCGLIB$$69a17158
        return clazz.getName().indexOf("$$EnhancerBy") == -1 ? clazz : clazz.getSuperclass();
    }
}
