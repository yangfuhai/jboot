package io.jboot.simples;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/myapp.ws")
public class WebsocketEndpoint {

    @OnMessage
    public void message(String message, Session session) {
        for (Session s : session.getOpenSessions()) {
            System.out.println("receive : " + message);
            s.getAsyncRemote().sendText(message +" ~~~ from server....");
        }
    }

}
