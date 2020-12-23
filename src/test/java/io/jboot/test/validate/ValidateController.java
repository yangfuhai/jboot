package io.jboot.test.validate;

import com.jfinal.aop.Aop;
import com.jfinal.core.Controller;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jboot.web.validate.*;

import javax.validation.constraints.*;

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


    public void test8(@Size(min = 100,max = 200) int para1) {
        renderText("test8");
    }


    public void test9(@Email String para1) {
        renderText("test9");
    }


    public void test10(@Max(10) int para1) {
        renderText("test10");
    }

    public void test11(@Min(10) int para1) {
        renderText("test11");
    }

    public void test12(@Min(10) @JsonBody("aaa.bbb.age") int para1) {
        renderText("test12");
    }

    public void test13(@Size(min = 10,max = 20) @JsonBody("aaa.bbb.age") int para1) {
        renderText("test13");
    }

    public void test14(@Size(min = 10,max = 20) @JsonBody("aaa.bbb.name") String name) {
        renderText("test14");
    }

    public void test15(@Digits(integer = 2,fraction = 3) @JsonBody("aaa.bbb.fff") float name) {
        renderText("test15");
    }

    public void test16() {
        Aop.get(TestValideService.class).calc(getInt("para"));
        renderText("test16" );
    }

    public void test17(@DecimalMax("200") @DecimalMin("100") int age) {
        renderText("test17" );
    }
}
