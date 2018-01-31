package shiro;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package shiro
 */
@RequestMapping(value = "/shiro",viewPath = "/htmls/shiro")
public class ShiroController extends JbootController {


    public void index() {
        render("index.html");
    }


}
