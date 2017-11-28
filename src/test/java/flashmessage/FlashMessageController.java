package flashmessage;

import io.jboot.Jboot;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package flashmessage
 */
@RequestMapping("/flashmessage")
public class FlashMessageController extends JbootController {

    public void index() {
        render("/htmls/flashmessage/index.html");
    }

    public void flash() {
        this.setFlashAttr("message", "flashMessage : test");
        redirect("/flashmessage");
    }

    public static void main(String[] args) {
        Jboot.run(args);
    }
}
