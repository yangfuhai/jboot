package io.jboot.test.cors;

import com.jfinal.kit.Ret;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.cors.EnableCORS;

@RequestMapping("/cors")
public class CorsController extends JbootController {

    /**
     * 此方法支持跨域请求
     */
    @EnableCORS
    public void method1() {
        renderJson(Ret.ok());
    }

    /**
     * 此方法不支持跨域请求
     */
    public void method2() {
        renderJson(Ret.ok());
    }
}
