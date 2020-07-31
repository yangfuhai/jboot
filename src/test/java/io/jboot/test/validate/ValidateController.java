package io.jboot.test.validate;

import com.jfinal.core.Controller;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.*;

@RequestMapping("/validate")
public class ValidateController extends Controller {


    public void index() {
        renderText("index");
    }


    @EmptyValidate(value = @Form(name = "form"), renderType = ValidateRenderType.JSON)
    public void test1() {
        renderText("test1");
    }


    @MatchesValidate(value = @MatchesForm(name = "email", regex = "\\w+@(\\w+.)+[a-z]{2,3}", message = "请输入正确的邮箱地址"))
    public void test2() {
        renderText("test2");
    }

    @MatchesValidate(value = @MatchesForm(name = "email", regex = "\\w+@(\\w+.)+[a-z]{2,3}"))
    public void test3() {
        renderText("test3");
    }


    @MatchesValidate(value = @MatchesForm(name = "email", regex = "\\w+@(\\w+.)+[a-z]{2,3}"), renderType = ValidateRenderType.JSON)
    public void test4() {
        renderText("test4");
    }
}
