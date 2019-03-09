package io.jboot.test.validate;

import com.jfinal.core.Controller;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jboot.web.validate.UrlParaValidate;
import io.jboot.web.validate.ValidateRenderType;

@RequestMapping("/validate")
public class ValidateController extends Controller {


    public void index(){
        renderText("index");
    }

    @UrlParaValidate
    public void test1(){
        renderText("test1");
    }

    @UrlParaValidate(renderType = ValidateRenderType.TEXT,message = "test2 was verification failed")
    public void test2(){
        renderText("test2");
    }

    @EmptyValidate(value = @Form(name = "form"),renderType = ValidateRenderType.JSON)
    public void test3(){
        renderText("test3");
    }
}
