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
package io.jboot.components.cache.interceptor;

import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.base.JbootDirectiveBase;

import java.io.IOException;

public class ParaDirective extends JbootDirectiveBase {

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Controller controller = JbootControllerContext.get();
        if (controller == null) {
            throw new IllegalStateException("#para(...) directive only use for controller." + getLocation());
        }

        String key = getPara(0, scope);
        String defaultValue = getPara(1, scope);

        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("#para(...) argument must not be empty." + getLocation());
        }

        String value = controller.getPara(key);
        if (StrUtil.isBlank(value)) {
            value = StrUtil.isNotBlank(defaultValue) ? defaultValue : "";
        }

        try {
            writer.write(value);
        } catch (IOException e) {
            LogKit.error(e.toString(), e);
        }
    }

    public static Object para(String key) {
        return para(key, null);
    }

    public static Object para(String key, Object defaultValue) {
        Controller controller = JbootControllerContext.get();
        if (controller == null) {
            throw new IllegalStateException("para(...) method only use for controller.");
        }

        String value = controller.get(key);

        if (StrUtil.isNumeric(value)) {
            return Long.valueOf(value);
        }

        if (StrUtil.isDecimal(value)) {
            return Double.parseDouble(value);
        }

        return StrUtil.isNotBlank(value) ? value : defaultValue;
    }
}

