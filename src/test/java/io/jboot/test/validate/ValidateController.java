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
}
