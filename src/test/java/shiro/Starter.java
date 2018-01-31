package shiro;

import io.jboot.Jboot;
import io.jboot.server.listener.JbootAppListenerBase;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package shiro
 */
public class Starter extends JbootAppListenerBase {

    @Override
    public void onJFinalStarted() {
        IniSecurityManagerFactory factory= new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(SecurityUtils.getSecurityManager());
    }

    public static void main(String[] args) {

        Jboot.run(args);
    }
}
