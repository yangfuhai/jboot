/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.core.http;

import io.jboot.core.config.annotation.PropertyModel;


@PropertyModel(prefix = "jboot.http")
public class JbootHttpConfig {
    public static final String TYPE_DEFAULT = "default";
    public static final String TYPE_HTTPCLIENT = "httpclient";
    public static final String TYPE_OKHTTP = "okhttp";

    public String type = TYPE_DEFAULT;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
