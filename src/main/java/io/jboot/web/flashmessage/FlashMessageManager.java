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

    private static final String FLASH_SESSION_ATTR = "_JBOOT_FLASH_";

    private static final String FLASH_COOKIE_ATTR = "_JBOOT_FLASH_";
    private static final String FLASH_COOKIE_VALUE = "1";


    private static final FlashMessageManager ME = new FlashMessageManager();

    public static FlashMessageManager me() {
        return ME;
    }

    public void renderTo(Controller controller) {
        if (!hasFlashMessage()) {
            return;
        }
        HashMap<String, Object> flash = controller.getSessionAttr(FLASH_SESSION_ATTR);
        if (flash == null || flash.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : flash.entrySet()) {
            controller.setAttr(entry.getKey(), entry.getValue());
        }
    }

    public void init(Controller controller) {
        HashMap flash = ((JbootController) controller).getFlashAttrs();
        if (flash != null) {
            controller.setSessionAttr(FLASH_SESSION_ATTR, flash);
            controller.setCookie(FLASH_SESSION_ATTR, FLASH_COOKIE_VALUE, 60);
        }
    }

    public void release(Controller controller) {
        if (!hasFlashMessage()) {
            return;
        }
        controller.removeSessionAttr(FLASH_SESSION_ATTR);
        controller.removeCookie(FLASH_COOKIE_ATTR);
    }

    /**
     * 查看是否有 FlashMessage
     * 因为对 FlashMessage操作是有"代价的"，需要去操作redis 等缓存
     * 所以应该先通过 cookie来判断，再执行
     *
     * @return
     */
    private boolean hasFlashMessage() {
        return FLASH_COOKIE_VALUE.equals(FLASH_COOKIE_ATTR);
    }
}
