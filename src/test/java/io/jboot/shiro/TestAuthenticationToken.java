package io.jboot.shiro;

import org.apache.shiro.authc.AuthenticationToken;


public class TestAuthenticationToken implements AuthenticationToken {
    @Override
    public Object getPrincipal() {
        return "Principal";
    }

    @Override
    public Object getCredentials() {
        return "Credentials";
    }
}
