package paravalidator;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.cache.annotation.Cacheable;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.controller.validate.CaptchaValidate;
import io.jboot.web.controller.validate.EmptyValidate;
import io.jboot.web.controller.validate.Form;

import javax.inject.Inject;
import javax.inject.Singleton;


@RequestMapping("/validator")
public class ControllerValidator extends Controller {


    public static void main(String[] args) {
        Jboot.setBootArg("jboot.server.port", "9999");
        Jboot.run(args);
    }

    @EmptyValidate(value = {
            @Form(name = "id", message = "id不能为空"),
    }, errorRedirect = "/validator/error")
    public void id() {
        renderJson(Ret.create("id", getPara("id")));
    }

    @EmptyValidate(value = {
            @Form(name = "name", message = "name不能为空"),
    }, errorRedirect = "/validator/error", isAjax = true)
    public void name() {
        renderJson(Ret.create("name", getPara("name")));
    }

    @CaptchaValidate(form = "captcha", errorRedirect = "/validator/error")
    public void captcha() {

    }

    @CaptchaValidate(form = "captcha", errorRedirect = "/validator/error", isAjax = true)
    public void captcha2() {

    }

    public void error() {
        renderText("出错了");
    }
}
