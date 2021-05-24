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
package io.jboot.web.json;

import io.jboot.app.config.annotation.ConfigModel;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@ConfigModel(prefix = "jboot.json")
public class JbootJsonConfig {

    private boolean camelCaseJsonStyleEnable = true;
    private boolean camelCaseToLowerCaseAnyway = false;
    private boolean skipNullValueField = true;
    private boolean skipModelAttrs = false;
    private boolean skipBeanGetters = false;
    private String timestampPattern;

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
}
