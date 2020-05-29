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
import com.alibaba.fastjson.parser.ParserConfig;
import com.jfinal.json.JFinalJson;
import io.jboot.Jboot;


public class JbootJson extends JFinalJson {

    private static boolean isCamelCaseJsonStyleEnable = Jboot.config(JbootWebConfig.class).isCamelCaseJsonStyleEnable();

    public JbootJson() {

        //完全禁用 autoType，提升安全性
        ParserConfig.getGlobalInstance().setSafeMode(true);

        //跳过 null 值输出到浏览器，提高传输性能
        setSkipNullValueField(true);

        //默认设置为 CamelCase 的属性模式
        if (isCamelCaseJsonStyleEnable) {
            setModelAndRecordFieldNameToCamelCase();
        }


    }

    @Override
    public <T> T parse(String jsonString, Class<T> type) {
        return JSON.parseObject(jsonString, type);
    }
}
