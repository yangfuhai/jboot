package io.jboot.test.captcha;

import com.jfinal.core.Controller;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/captcha")
public class CaptchaController extends Controller {

    public void index(){
        renderCaptcha();
    }
}
