package top.codewood.websocket.endpoint;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import top.codewood.websocket.config.WebSocketConfig;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint(value = "/socket/one", configurator = WebSocketConfig.class)
public class SocketOneEndpoint {

    static final Gson gson = new Gson();

    static final Logger LOGGER = LoggerFactory.getLogger(SocketOneEndpoint.class);

    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    private static final Map<String, Session> SESSION_MAP = new HashMap<>();

    {
        LOGGER.info("SocketOneEndpoint instantiated");
    }

    @OnOpen
    public void onOpen(Session session) {
        ONLINE_COUNT.incrementAndGet();
        LOGGER.info("新加入连接：{}，当前在线人数：{}", session.getId(), ONLINE_COUNT.get());
        LOGGER.info("current http session: {}", session.getUserProperties().get("httpSessionId"));
        SESSION_MAP.put(session.getId(), session);

        JsonObject json = new JsonObject();
        json.addProperty("type", "connected");
        json.addProperty("sessionId", session.getId());
        sendMessage(session, json.toString());
    }

    @OnClose
    public void onClose(Session session) {
        ONLINE_COUNT.decrementAndGet();
        SESSION_MAP.remove(session.getId());
        LOGGER.info("id: {} 退出链接， 当前在线人数：{}", session.getId(), ONLINE_COUNT.get());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        LOGGER.info("接收到[{}]信息：{}", session.getId(), message);
        sendMessage(session, message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        LOGGER.error("error.");
        error.printStackTrace();
    }

    private static void sendMessage(Session session, String message) {
        try {
            synchronized (session) {
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            LOGGER.error("id:{}, 发送信息失败：{}", session.getId(), e.getMessage());
        }
    }

    public static void sendMessage(String sessionId, String message) {
        Session session = SESSION_MAP.get(sessionId);
        if (session == null) {
            throw new RuntimeException(String.format("会话[%s]不存在", sessionId));
        }
        sendMessage(session, message);
    }

    public static Collection<String> getAllSessionIds() {
        return SESSION_MAP.keySet();
    }


}
