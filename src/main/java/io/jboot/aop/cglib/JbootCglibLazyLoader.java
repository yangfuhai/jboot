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
package io.jboot.aop.cglib;

import io.jboot.aop.JbootAopFactory;
import net.sf.cglib.proxy.LazyLoader;

import java.lang.reflect.Field;

public class JbootCglibLazyLoader implements LazyLoader {

    private static JbootAopFactory factory = JbootAopFactory.me();

    private Object targetObject;
    private Field field;

    public JbootCglibLazyLoader(Object targetObject, Field field) {
        this.targetObject = targetObject;
        this.field = field;
    }

    @Override
    public Object loadObject() throws Exception {
        return factory.createFieldObjectNormal(targetObject, field);
    }


}
