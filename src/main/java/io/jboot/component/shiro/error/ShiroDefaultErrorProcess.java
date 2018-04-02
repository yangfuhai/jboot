package io.jboot.component.shiro.error;

import com.jfinal.core.Controller;
import io.jboot.Jboot;
import io.jboot.component.shiro.JbootShiroConfig;
import io.jboot.utils.StringUtils;

/**
 * 默认错误处理器
 */
public class ShiroDefaultErrorProcess implements ShiroErrorProcess {

    private JbootShiroConfig config = Jboot.config(JbootShiroConfig.class);

    @Override
    public void doProcessUnauthenticated(Controller controller) {
        if (StringUtils.isBlank(config.getLoginUrl())) {
            controller.renderError(401);
            return;
        }
        controller.redirect(config.getLoginUrl());
    }

    @Override
    public void doProcessuUnauthorization(Controller controller) {
        if (StringUtils.isBlank(config.getUnauthorizedUrl())) {
            controller.renderError(403);
            return;
        }
        controller.redirect(config.getUnauthorizedUrl());
    }
}
