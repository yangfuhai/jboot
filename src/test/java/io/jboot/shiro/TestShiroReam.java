package io.jboot.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.realm.Realm;


public class TestShiroReam implements Realm {

    @Override
    public String getName() {
        System.out.println("MyRealm.getName()");
        return null;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        System.out.println("MyRealm.supports:" + token);
        return true;
    }

    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("MyRealm.getAuthenticationInfo" + token);
        return new SimpleAuthenticationInfo("1", "2", "3");
    }

}