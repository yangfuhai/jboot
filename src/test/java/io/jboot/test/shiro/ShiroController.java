package io.jboot.test.shiro;


import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;

@RequestMapping(value = "/shiro", viewPath = "/htmls/shiro")
public class ShiroController extends JbootController {


    public void index() {
        renderText("index");
    }

    public void login() {
        renderText("login");
    }

    public void doLogin() {
        Subject subject = SecurityUtils.getSubject();
        // 默认为admin登陆
        UsernamePasswordToken token = new UsernamePasswordToken(getPara("username", "admin"), "123");
        subject.login(token);

//        subject.isAuthenticated();
//        subject.isPermitted()

        renderText("logined success");
    }

    public void logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        renderText("logouted success");
    }

    @RequiresAuthentication
    public void usercenter() {
        renderText("usercenter");
    }


    @RequiresGuest
    public void guest() {
        renderText("guest");
    }

    @RequiresRoles("editor")
    public void editor() {
        renderText("editor");
    }

    @RequiresRoles("admin")
    public void admin() {
        renderText("admin");
    }

    @RequiresPermissions("all:read")
    public void readAll() {
        renderText("all");
    }

    @RequiresPermissions("news:read")
    public void readNews() {
        renderText("news");
    }
}
