package io.jboot.test.shiro;


import io.jboot.app.JbootApplication;

public class ShrioApp {

    public static void main(String[] args) {

        JbootApplication.setBootArg("jboot.shiro.loginUrl","/shiro/doLogin");
        JbootApplication.setBootArg("jboot.shiro.ini","shiro.ini");
        JbootApplication.run(args);
    }
}
