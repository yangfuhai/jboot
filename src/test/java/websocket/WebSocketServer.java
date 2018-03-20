package websocket;

import io.jboot.utils.ArrayUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package websocket
 */
@ServerEndpoint("/myWebsocketServer")
public class WebSocketServer {


    static Map<String, CopyOnWriteArraySet<Session>> rooms = new ConcurrentHashMap<>();

    public static void sendMessage(String roomId, String message) {
        CopyOnWriteArraySet<Session> sessions = rooms.get(roomId);

        if (ArrayUtils.isNullOrEmpty(sessions)) {
            return;
        }
        for (Session session : sessions) {
            if (session.isOpen()) {
                session.getAsyncRemote().sendObject(message);
            } else {
                sessions.remove(session);
                quietClose(session);
            }
        }
    }

    public static void close(Session session) {

        String roomId = session.getRequestParameterMap().get("roomId").toString();
        CopyOnWriteArraySet<Session> sessions = rooms.get(roomId);

        if (ArrayUtils.isNotEmpty(sessions)) {
            sessions.remove(session);
        }

        quietClose(session);


    }

    private static void quietClose(Session session) {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 有新的连接的时候
     */
    @OnOpen
    public void onOpen(Session session) {
        String roomId = session.getRequestParameterMap().get("roomId").toString();
        CopyOnWriteArraySet<Session> sessions = rooms.get(roomId);
        if (sessions == null) {
            synchronized (roomId) {
                if (sessions == null) {
                    sessions = new CopyOnWriteArraySet<>();
                }
            }
        }
        sessions.add(session);

        rooms.put(roomId, sessions);
        System.out.println("有新的连接进来");
    }

    /**
     * 收到客户端消息时触发
     *
     * @param message
     * @return
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println("接受到新的消息:" + message);
        String roomId = session.getRequestParameterMap().get("roomId").toString();
        sendMessage(roomId, message);
    }

    /**
     * 异常时触发
     *
     * @param session
     */
    @OnError
    public void onError(Throwable throwable, Session session) {
        System.out.print("onError : " + session.getId() + throwable.toString());
        close(session);
    }

    /**
     * 关闭连接时触发
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        System.out.print("onClose :  " + session.getId());
        close(session);
    }
}
