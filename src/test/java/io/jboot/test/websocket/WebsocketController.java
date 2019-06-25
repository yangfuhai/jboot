package io.jboot.test.websocket;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;


@RequestMapping("/websocketdemo")
public class WebsocketController extends JbootController {

    public void index() {
        render("/websocket.html");
    }

}
