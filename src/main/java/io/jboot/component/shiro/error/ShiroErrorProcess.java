package io.jboot.component.shiro.error;

import com.jfinal.core.Controller;

/**
 * Shiro 认证授权错误处理
 */
public interface ShiroErrorProcess {

    /**
     * 未认证处理
     *
     * @param controller
     */
    public void doProcessUnauthenticated(Controller controller);

    /**
     * 未授权处理
     *
     * @param controller
     */
    public void doProcessuUnauthorization(Controller controller);

}
