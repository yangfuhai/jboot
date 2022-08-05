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
package io.jboot.web.render;

import com.jfinal.render.JsonRender;
import io.jboot.JbootConsts;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class JbootJsonRender extends JsonRender {

    static {
        excludedAttrs.add(JbootConsts.ATTR_CONTEXT_PATH);
    }

    public JbootJsonRender() {
    }

    public JbootJsonRender(String key, Object value) {
        super(key, value);
    }

    public JbootJsonRender(String[] attrs) {
        super(attrs);
    }

    public JbootJsonRender(String jsonText) {
        super(jsonText);
    }

    public JbootJsonRender(Object object) {
        super(object);
    }

}
