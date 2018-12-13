package shiro;

import io.jboot.Jboot;
import io.jboot.app.listener.JbootAppListenerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package shiro
 */
public class Starter extends JbootAppListenerBase {


    public static void main(String[] args) {

        Jboot.setBootArg("jboot.shiro.loginUrl","/shiro/doLogin");

        Jboot.run(args);
    }
}
