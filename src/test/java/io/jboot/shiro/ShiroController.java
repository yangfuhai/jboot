package io.jboot.shiro;


import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.subject.Subject;

@RequestMapping(value = "/shiro",viewPath = "/htmls/shiro")
public class ShiroController extends JbootController {


    public void index() {
        renderText("index");
    }

    public void login(){
        renderText("login");
    }

    public void doLogin(){

        Subject subject = SecurityUtils.getSubject();
        subject.login(new TestAuthenticationToken());

//        subject.isAuthenticated();
//        subject.isPermitted()

        renderText("logined");

    }

    public void logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        renderText("logouted");
    }

    @RequiresAuthentication
    public void usercenter(){
        renderText("usercenter");
    }


    @RequiresGuest
    public void guest(){
        renderText("guest");
    }

}
