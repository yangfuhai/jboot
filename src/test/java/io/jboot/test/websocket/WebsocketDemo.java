package io.jboot.test.websocket;

import io.jboot.app.JbootApplication;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/myapp.ws")
public class WebsocketDemo {

    /**
     * 运行 main 方法后，访问 http://127.0.0.1:8888/websocketdemo 查看效果
     * html 的相关 js 在 resources 下的 websocket.html
     *
     * @param args
     */
    public static void main(String[] args) {

        JbootApplication.setBootArg("undertow.host", "127.0.0.1");
        JbootApplication.setBootArg("undertow.port", 8888);
        JbootApplication.setBootArg("jboot.web.webSocketEndpoint", "io.jboot.test.websocket.WebsocketDemo");

        JbootApplication.run(args);
    }


    @OnMessage
    public void message(String message, Session session) {
        for (Session s : session.getOpenSessions()) {
            System.out.println("receive : " + message);
            s.getAsyncRemote().sendText(message);
        }
    }

}
