package io.jboot.simples;

import io.jboot.app.JbootApplication;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


public class MainStarter {

    /**
     * 运行 main 方法后，访问 http://127.0.0.1:8888/ 查看效果
     * html 的相关 js 在 index.html 里
     *
     * @param args
     */
    public static void main(String[] args) {

        JbootApplication.setBootArg("undertow.host", "127.0.0.1");
        JbootApplication.setBootArg("undertow.port", 8888);
        JbootApplication.setBootArg("jboot.web.webSocketEndpoint", "io.jboot.simples.WebsocketEndpoint");

        JbootApplication.run(args);
    }



}
