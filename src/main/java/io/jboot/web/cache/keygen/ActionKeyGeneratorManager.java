/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.cache.keygen;

import io.jboot.Jboot;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.StrUtils;
import io.jboot.web.JbootWebConfig;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.cache.keygen
 */
public class ActionKeyGeneratorManager {

    private final static ActionKeyGeneratorManager me = new ActionKeyGeneratorManager();

    public static ActionKeyGeneratorManager me() {
        return me;
    }

    private JbootWebConfig webConfig = Jboot.config(JbootWebConfig.class);

    private IActionKeyGenerator generator;

    public IActionKeyGenerator getGenerator() {

        if (generator == null) {
            generator = createGenerator();
        }
        return generator;


    }

    private IActionKeyGenerator createGenerator() {

        String type = webConfig.getActionCacheKeyGeneratorType();

        if (StrUtils.isBlank(type)) {
            return new DefaultActionKeyGeneratorImpl();
        }

        if (JbootWebConfig.ACTION_CACHE_KEYGENERATOR_TYPE_DEFAULT.equals(type)) {
            return new DefaultActionKeyGeneratorImpl();
        }

        IActionKeyGenerator generator = JbootSpiLoader.load(IActionKeyGenerator.class, type);
        if (generator == null) {
            throw new JbootIllegalConfigException("can not load [" + IActionKeyGenerator.class.getName() + "] from spi with type:" + type);
        }

        return generator;
    }


}
