package io.jboot.test.validate;

import com.jfinal.core.Controller;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@RequestMapping("/validate")
public class ValidateController extends Controller {


    public void index() {
        renderText("index");
    }


    @EmptyValidate(value = @Form(name = "form"), renderType = ValidateRenderType.JSON)
    public void test1() {
        renderText("test1");
    }


    @RegexValidate(value = @RegexForm(name = "email", regex = Regex.EMAIL, message = "请输入正确的邮箱地址"))
    public void test2() {
        renderText("test2");
    }

    @RegexValidate(value = @RegexForm(name = "email", regex = Regex.EMAIL))
    public void test3() {
        renderText("test3");
    }


    @RegexValidate(value = @RegexForm(name = "email", regex = Regex.EMAIL), renderType = ValidateRenderType.JSON)
    public void test4() {
        renderText("test4");
    }

    public void test5(@NotBlank String para1) {
        renderText("test5");
    }

    public void test6(@NotNull String para1) {
        renderText("test6");
    }

    public void test7(@Pattern(regexp = Regex.EMAIL) String para1) {
        renderText("test7");
    }
}
