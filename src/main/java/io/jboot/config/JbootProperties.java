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
package io.jboot.config;


import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import io.jboot.Jboot;
import io.jboot.config.annotation.PropertieConfig;
import io.jboot.config.annotation.PropertieConfigField;
import io.jboot.utils.ClassNewer;
import io.jboot.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JBoot的配置文件
 * Created by michael on 17/3/21.
 */
public class JbootProperties {


    public static ConcurrentHashMap<Class, Object> configs = new ConcurrentHashMap<>();


    public static <T> T get(Class<T> clazz) {

        Object obj = configs.get(clazz);
        if (obj != null) {
            return (T) obj;
        }

        obj = ClassNewer.newInstance(clazz);

        String prefix = null;
        PropertieConfig propertieConfig = clazz.getAnnotation(PropertieConfig.class);

        if (propertieConfig != null && StringUtils.isNotBlank(propertieConfig.prefix())) {
            prefix = propertieConfig.prefix();
        }

        String propertieFile = propertieConfig.file();

        Prop prop = PropKit.use(propertieFile);
        initModeProp(prop, propertieFile);

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {

            PropertieConfigField jbootConfigField = field.getAnnotation(PropertieConfigField.class);

            String key = field.getName();


            if (jbootConfigField != null && StringUtils.isNotBlank(jbootConfigField.key())) {
                key = jbootConfigField.key().trim();
            }


            if (StringUtils.isNotBlank(prefix)) {
                key = prefix.trim() + "." + key;
            }

            String value = Jboot.getBootArg(key);
            if (StringUtils.isBlank(value)) {
                value = prop.get(key);
            }

            if (StringUtils.isNotBlank(value)) {
                setValueToObj(obj, field, value);
            }

        }

        configs.put(clazz, obj);

        return (T) obj;
    }


    private static void initModeProp(Prop prop, String file) {
        String mode = PropKit.use("jboot.properties").get("jboot.mode");
        if (StringUtils.isBlank(mode)) {
            return;
        }

        Prop modeProp = null;
        try {
            String p = String.format("%s-%s.properties", file.substring(0, file.lastIndexOf(".") ), mode);
            modeProp = PropKit.use(p);
        } catch (Throwable ex) {
        }

        if (modeProp == null) {
            return;
        }

        prop.getProperties().putAll(modeProp.getProperties());
    }

    private static void setValueToObj(Object obj, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
