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
package io.jboot.web.flashmessage;

import com.jfinal.core.Controller;
import io.jboot.web.controller.JbootController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用于对 FlashMessage 的管理
 * @Package io.jboot.web.flashmessage
 */
public class FlashMessageManager {

    private static final String FLASH_SESSION_ATTR = "_JFM_"; // JFM : jboot flash message

    private static final FlashMessageManager ME = new FlashMessageManager();

    public static FlashMessageManager me() {
        return ME;
    }

    public void renderTo(Controller controller) {
        HashMap<String, Object> flash = controller.getSessionAttr(FLASH_SESSION_ATTR);
        if (flash == null || flash.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : flash.entrySet()) {
            controller.setAttr(entry.getKey(), entry.getValue());
        }
    }

    public void init(Controller controller) {
        if (!(controller instanceof JbootController)) {
            return;
        }
        HashMap flash = ((JbootController) controller).getFlashAttrs();
        if (flash == null || flash.isEmpty()) {
            return;
        }
        controller.setSessionAttr(FLASH_SESSION_ATTR, flash);
    }


    public void release(Controller controller) {
        if (controller.getSessionAttr(FLASH_SESSION_ATTR) == null) {
            return;
        }
        controller.removeSessionAttr(FLASH_SESSION_ATTR);
    }

}
