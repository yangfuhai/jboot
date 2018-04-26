package shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package shiro
 */
public class MyAuthenticationToken implements AuthenticationToken {

    @Override
    public Object getPrincipal() {
        return "MyPrincipal";
    }

    @Override
    public Object getCredentials() {
        return "MyCredentials";
    }
}
