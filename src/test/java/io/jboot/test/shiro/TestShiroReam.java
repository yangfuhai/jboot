package io.jboot.test.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;


public class TestShiroReam extends AuthorizingRealm {

    @Override
    public String getName() {
        System.out.println("MyRealm.getName()");
        return "TestShiroReam";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        System.out.println("MyRealm.supports:" + token);
        return true;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        return new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRole(username); // admin | editor
        if ("admin".equalsIgnoreCase(username)) {
            info.addStringPermission("all:read");
        } else if ("editor".equalsIgnoreCase(username)) {
            info.addStringPermission("news:read");
        }
        return info;
    }
}