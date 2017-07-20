/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.aop.interceptor.cache;

import com.jfinal.template.Engine;
import io.jboot.utils.StringUtils;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Kits {

    static final Engine ENGINE = new Engine("JbootCacheRender");

    /**
     * use jfinal engine render text
     *
     * @param template
     * @param method
     * @param arguments
     * @return
     */
    static String engineRender(String template, Method method, Object[] arguments) {

        Annotation[][] annotationss = method.getParameterAnnotations();
        Map<String, Object> datas = new HashMap();
        for (int i = 0; i < annotationss.length; i++) {
            for (int j = 0; j < annotationss[i].length; j++) {
                Annotation annotation = annotationss[i][j];
                if (annotation.annotationType() == Named.class) {
                    Named named = (Named) annotation;
                    datas.put(named.value(), arguments[i]);
                } else if (annotation.annotationType() == com.google.inject.name.Named.class) {
                    com.google.inject.name.Named named = (com.google.inject.name.Named) annotation;
                    datas.put(named.value(), arguments[i]);
                }
            }
        }

        return ENGINE.getTemplateByString(template).renderToString(datas);
    }

    static String buildCacheKey(String key, Class clazz, Method method, Object[] arguments) {
        if (StringUtils.isBlank(key)) {
            return String.format("%s#%s", clazz.getName(), method.getName());
        }

        if (!key.contains("#(") || !key.contains(")")) {
            return key;
        }

        return Kits.engineRender(key, method, arguments);
    }
}
