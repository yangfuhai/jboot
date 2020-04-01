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
package io.jboot.web;

import com.alibaba.fastjson.JSON;
import com.jfinal.json.JFinalJson;
import com.jfinal.kit.StrKit;
import io.jboot.Jboot;

import java.util.Iterator;
import java.util.Map;


public class JbootJson extends JFinalJson {

    private static JbootWebConfig config;

    public static JbootWebConfig getConfig() {
        if (config == null) {
            config = Jboot.config(JbootWebConfig.class);
        }
        return config;
    }

    @Override
    protected String mapToJson(Map map, int depth) {
        optimizeMapAttrs(map);

        if(getConfig().getCamelCaseJsonStyleEnable()){
            return toCamelCase(map, depth);
        }
        return map == null || map.isEmpty() ? "null" : super.mapToJson(map, depth);
    }

    private String toCamelCase(Map map, int depth){
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        Iterator iter = map.entrySet().iterator();

        sb.append('{');
        while(iter.hasNext()){
            if(first)
                first = false;
            else
                sb.append(',');

            Map.Entry entry = (Map.Entry)iter.next();
            toKeyValue(StrKit.toCamelCase(String.valueOf(entry.getKey())),entry.getValue(), sb, depth);
        }
        sb.append('}');
        return sb.toString();
    }


    /**
     * 优化 map 的属性
     *
     * @param map
     */
    private void optimizeMapAttrs(Map map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            //移除 null 值的属性
            if (entry.getValue() == null) {
                iter.remove();
            }
        }
    }


    @Override
    public <T> T parse(String jsonString, Class<T> type) {
        return JSON.parseObject(jsonString, type);
    }
}
