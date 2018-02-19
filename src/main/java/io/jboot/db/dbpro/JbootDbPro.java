/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.db.dbpro;

import com.jfinal.plugin.activerecord.DbPro;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.Jboot;
import io.jboot.component.hystrix.JbootHystrixCommand;
import io.jboot.db.JbootDbHystrixFallbackListener;
import io.jboot.db.JbootDbHystrixFallbackListenerDefault;
import io.jboot.db.model.JbootModelConfig;
import io.jboot.utils.ClassKits;
import io.jboot.utils.StringUtils;
import io.shardingjdbc.core.api.HintManager;
import io.shardingjdbc.core.hint.HintManagerHolder;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.db.dbpro
 */
public class JbootDbPro extends DbPro {

    public JbootDbPro() {
    }

    public JbootDbPro(String configName) {
        super(configName);
    }

    @Override
    public List<Record> find(String sql, Object... paras) {

        if (!JbootModelConfig.getConfig().isHystrixEnable()) {
            return super.find(sql, paras);
        }

        final HintManager hintManager = HintManagerHolder.get();

        return Jboot.hystrix(new JbootHystrixCommand("sql:" + sql, JbootModelConfig.getConfig().getHystrixTimeout()) {
            @Override
            protected Object run() throws Exception {
                try {
                    HintManagerHolder.setHintManager(hintManager);
                    return JbootDbPro.super.find(sql, paras);
                } finally {
                    HintManagerHolder.clear();
                }
            }

            @Override
            public Object getFallback() {
                return getHystrixFallbackListener().onFallback(sql, paras, this, this.getExecutionException());
            }
        });

    }


    private JbootDbHystrixFallbackListener fallbackListener = null;

    public JbootDbHystrixFallbackListener getHystrixFallbackListener() {

        if (fallbackListener != null) {
            return fallbackListener;
        }

        if (!StringUtils.isBlank(JbootModelConfig.getConfig().getHystrixFallbackListener())) {
            fallbackListener = ClassKits.newInstance(JbootModelConfig.getConfig().getHystrixFallbackListener());
        }

        if (fallbackListener == null) {
            fallbackListener = new JbootDbHystrixFallbackListenerDefault();
        }

        return fallbackListener;
    }
}
