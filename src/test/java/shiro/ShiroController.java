package shiro;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.subject.Subject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package shiro
 */
@RequestMapping(value = "/shiro",viewPath = "/htmls/shiro")
public class ShiroController extends JbootController {


    public void index() {
        render("index.html");
    }

    public void login(){
        Subject subject = SecurityUtils.getSubject();
        subject.login(new MyAuthenticationToken());

//        subject.isAuthenticated();
//        subject.isPermitted()

        renderText("logined");
    }

    public void doLogin(){
        renderText("doLogin");
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
