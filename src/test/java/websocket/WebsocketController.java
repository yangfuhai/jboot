package websocket;

import io.jboot.Jboot;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package websocket
 */
@RequestMapping("/websocket")
public class WebsocketController extends JbootController {

    public void index() {
        render("/htmls/websocket/index.html");
    }


    public static void main(String[] args) {
        Jboot.setBootArg("jboot.web.websocketEnable", true);
        Jboot.run(args);
    }

}
