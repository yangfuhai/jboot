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
package io.jboot.app.undertow;

import com.jfinal.server.undertow.hotswap.HotSwapResolver;
import io.jboot.app.config.JbootConfigManager;


public class JbootHotSwapResolver extends HotSwapResolver {

    protected String[] unHotSwapClassPrefix;

    public JbootHotSwapResolver(String[] classPathDirs) {
        super(classPathDirs);
        initUnHotSwapClassPrefix();
    }

    private void initUnHotSwapClassPrefix() {
        String string = JbootConfigManager.me().getConfigValue("undertow.unHotSwapClassPrefix");
        if (string != null && string.trim().length() > 0) {
            unHotSwapClassPrefix = string.split(",");
        }
    }

    @Override
    public boolean isHotSwapClass(String className) {
        if (unHotSwapClassPrefix != null) {
            for (String prefix : unHotSwapClassPrefix) {
                if (prefix.trim().length() > 0 && className.startsWith(prefix.trim())) {
                    return false;
                }
            }
        }
        return super.isHotSwapClass(className);
    }
}
