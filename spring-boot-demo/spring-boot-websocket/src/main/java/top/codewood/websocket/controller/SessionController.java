package top.codewood.websocket.controller;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.codewood.websocket.endpoint.SocketOneEndpoint;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RestController
@RequestMapping("session")
public class SessionController {

    final Logger logger = LoggerFactory.getLogger(SessionController.class);

    final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    @RequestMapping("one/send")
    public String send(String sessionId, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "message");
        json.addProperty("message", message);
        SocketOneEndpoint.sendMessage(sessionId, json.toString());
        return "success";
    }

    @RequestMapping("one/count")
    public String count(String sessionId,  @RequestParam(value = "times", required = false, defaultValue = "1") int times) {

        new Thread(() -> {
            for (int i = 0; i < times; i++) {
                EXECUTOR_SERVICE.execute(() -> {
                    JsonObject json = new JsonObject();
                    json.addProperty("type", "count");
                    SocketOneEndpoint.sendMessage(sessionId, json.toString());
                });
            }
        }).start();

        return "success";
    }

    @RequestMapping("one/counts")
    public String count(String[] sessionIds,  @RequestParam(value = "times", required = false, defaultValue = "1") int times) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "count");
        for (String sessionId : sessionIds) {
            new Thread(() -> {
                for (int i = 0; i < times; i++) {
                    EXECUTOR_SERVICE.execute(() -> {
                        SocketOneEndpoint.sendMessage(sessionId, json.toString());
                    });
                }
            }).start();
        }

        return "success";
    }

    @RequestMapping("one/sessionIds")
    public Collection<String> getSessionIds() {
        return SocketOneEndpoint.getAllSessionIds();
    }

}
