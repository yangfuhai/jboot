package spi;

import io.jboot.Jboot;
import io.jboot.server.listener.JbootAppListenerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package shiro
 */
public class Starter extends JbootAppListenerBase {


    public static void main(String[] args) {

        Jboot.setBootArg("jboot.server.type","myserver");

        Jboot.run(args);

        System.out.println(Jboot.me().getServer());
    }
}
