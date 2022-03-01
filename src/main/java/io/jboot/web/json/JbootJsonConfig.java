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
package io.jboot.web.json;

import io.jboot.app.config.annotation.ConfigModel;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@ConfigModel(prefix = "jboot.json")
public class JbootJsonConfig {

    //model 和 record 是否自动转为驼峰格式
    //当配置此项为 false 的时候，需要配置 skipBeanGetters 为 true，否则出现个别相同数据库字段输出两次的情况
    private boolean camelCaseJsonStyleEnable = true;

    //是否把所有的信息都转为驼峰格式，包括 map
    private boolean camelCaseToLowerCaseAnyway = false;

    //是否跳过 null 值
    private boolean skipNullValueField = true;

    //是否跳过 model attrs，而只有使用 getter 来生成
    private boolean skipModelAttrs = false;

    //是否跳过 getter 方法
    private boolean skipBeanGetters = false;

    //时间格式化
    private String timestampPattern;

    //转换深度，防止且套引用导致死循环转换
    private int depth = 16;

    public boolean isCamelCaseJsonStyleEnable() {
        return camelCaseJsonStyleEnable;
    }

    public void setCamelCaseJsonStyleEnable(boolean camelCaseJsonStyleEnable) {
        this.camelCaseJsonStyleEnable = camelCaseJsonStyleEnable;
    }

    public boolean isCamelCaseToLowerCaseAnyway() {
        return camelCaseToLowerCaseAnyway;
    }

    public void setCamelCaseToLowerCaseAnyway(boolean camelCaseToLowerCaseAnyway) {
        this.camelCaseToLowerCaseAnyway = camelCaseToLowerCaseAnyway;
    }

    public boolean isSkipNullValueField() {
        return skipNullValueField;
    }

    public void setSkipNullValueField(boolean skipNullValueField) {
        this.skipNullValueField = skipNullValueField;
    }

    public boolean isSkipModelAttrs() {
        return skipModelAttrs;
    }

    public void setSkipModelAttrs(boolean skipModelAttrs) {
        this.skipModelAttrs = skipModelAttrs;
    }

    public boolean isSkipBeanGetters() {
        return skipBeanGetters;
    }

    public void setSkipBeanGetters(boolean skipBeanGetters) {
        this.skipBeanGetters = skipBeanGetters;
    }

    public String getTimestampPattern() {
        return timestampPattern;
    }

    public void setTimestampPattern(String timestampPattern) {
        this.timestampPattern = timestampPattern;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
