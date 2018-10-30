package paravalidator;

import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import io.jboot.Jboot;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.controller.validate.*;
import io.jboot.web.cors.EnableCORS;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


@RequestMapping("/validator")
@EnableCORS
public class ControllerValidator extends Controller {


    public static void main(String[] args) {
        Jboot.setBootArg("jboot.server.port", "9999");
        Jboot.run(args);
    }

    @EmptyValidate(value = {
            @Form(name = "id", message = "id不能为空"),
    }, renderType = ValidateRenderType.REDIRECT, message = "/validator/error")
    public void id() {
        renderJson(Ret.create("id", getPara("id")));
    }

    @EmptyValidate(value = {
            @Form(name = "name", message = "name不能为空"),
    }, renderType = ValidateRenderType.JSON)
    public void name() {
        renderJson(Ret.create("name", getPara("name")));
    }

    @EmptyValidate(value = {
            @Form(name = "name.abc", message = "name不能为空", type = FormType.RAW_DATA),
    }, renderType = ValidateRenderType.JSON)
    public void raw() {
        renderJson(Ret.create("name", getRawData()));
    }

    @EmptyValidate(value = {
            @Form(name = "name", message = "name不能为空", type = "abc"),
    }, renderType = ValidateRenderType.JSON)
    public void rawErrorType() {
        renderJson(Ret.create("name", getRawData()));
    }

    @CaptchaValidate(form = "captcha", renderType = ValidateRenderType.TEXT, message = "/validator/error")
    public void captcha() {

    }

    @CaptchaValidate(form = "captcha", renderType = ValidateRenderType.JSON)
    public void captcha2() {

    }

    public void error() {
        renderText("出错了");
    }

}
