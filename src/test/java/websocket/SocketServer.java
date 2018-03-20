package websocket;

import io.jboot.utils.ArrayUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
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
public class SocketServer {


    static Map<String, CopyOnWriteArraySet<Session>> rooms = new ConcurrentHashMap<>();

    public static void sendMessage(String roomId, String message) {
        CopyOnWriteArraySet<Session> sessions = rooms.get(roomId);

        if (ArrayUtils.isNullOrEmpty(sessions)) {
            return;
        }
        for (Session s : sessions) {
            s.getAsyncRemote().sendObject(message);
        }
    }


    /**
     * 有新的连接的时候
     */
    @OnOpen
    public void onOpen(Session session) {
        String roomId = session.getRequestParameterMap().get("roomId").toString();
        System.out.println("room id: " + roomId);

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
        System.out.print("onError" + throwable.toString());
    }

    /**
     * 关闭连接时触发
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        System.out.print("onClose ");
    }
}
