package top.codewood.websocket.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import top.codewood.websocket.config.WebSocketConfig;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint(value = "/socket/one", configurator = WebSocketConfig.class)
public class SocketOneEndpoint {

    static final Logger LOGGER = LoggerFactory.getLogger(SocketOneEndpoint.class);

    private static final AtomicInteger ONLILNE_COUNT = new AtomicInteger(0);

    {
        LOGGER.info("SocketOneEndpoint instantiated");
    }

    @OnOpen
    public void onOpen(Session session) {
        ONLILNE_COUNT.incrementAndGet();
        LOGGER.info("新加入连接：{}，当前在线人数：{}", session.getId(), ONLILNE_COUNT.get());
        LOGGER.info("current http session: {}", session.getUserProperties().get("httpSessionId"));
    }

    @OnClose
    public void onClose(Session session) {
        ONLILNE_COUNT.decrementAndGet();
        LOGGER.info("id: {} 退出链接， 当前在线人数：{}", session.getId(), ONLILNE_COUNT.get());
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

    private void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            LOGGER.error("id:{}, 发送信息失败：{}", session.getId(), e.getMessage());
        }
    }

}
