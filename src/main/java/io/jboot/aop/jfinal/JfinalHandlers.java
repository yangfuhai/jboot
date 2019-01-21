/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.aop.jfinal;

import com.jfinal.aop.Aop;
import com.jfinal.config.Handlers;
import com.jfinal.core.ActionHandler;
import com.jfinal.handler.Handler;

/**
 * Jfinal Handlers 的代理类，方便为Handler插件的自动注入功能
 */
public class JfinalHandlers {

    private final Handlers handlers;

    public JfinalHandlers(Handlers handlers) {
        this.handlers = handlers;
    }

    public JfinalHandlers add(Handler handler) {
        Aop.inject(handler);
        handlers.add(handler);
        return this;
    }

    public JfinalHandlers add(int index, Handler handler) {
        Aop.inject(handler);
        handlers.getHandlerList().add(index, handler);
        return this;
    }

    public JfinalHandlers setActionHandler(ActionHandler actionHandler) {
        Aop.inject(actionHandler);
        handlers.setActionHandler(actionHandler);
        return this;
    }

    public Handlers getHandlers() {
        return handlers;
    }
}
