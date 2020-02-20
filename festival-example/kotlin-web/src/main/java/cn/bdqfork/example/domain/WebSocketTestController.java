package cn.bdqfork.example.domain;

import cn.bdqfork.kotlin.web.route.annotation.*;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2020/2/19
 */
@Singleton
@ServerEndpoint("/test")
@RouteController("/test/websocket")
public class WebSocketTestController {
    private static Logger log = LoggerFactory.getLogger(WebSocketTestController.class);
    private static Map<String, ServerWebSocket> serverWebSocketMap = new ConcurrentHashMap<>();

    @OnOpen
    public void open(ServerWebSocket serverWebSocket) {
        log.info("websocket open with id {}.", serverWebSocket.binaryHandlerID());
        serverWebSocketMap.putIfAbsent("1", serverWebSocket);
    }

    @OnActive
    public void active(ServerWebSocket serverWebSocket, WebSocketFrame webSocketFrame) {
        log.info("active......");
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("type", 1);
        jsonObject.put("data", "hello");
        serverWebSocket.writeTextMessage(jsonObject.encodePrettily());
    }

    @OnClose
    public void close(ServerWebSocket serverWebSocket) {
        log.info("close websocket!");
    }

    @GetMapping("/send")
    public void sendInfo(@Param("id") String id, @Param("info") String info, HttpServerResponse response) {
        ServerWebSocket serverWebSocket = serverWebSocketMap.get(id);
        serverWebSocket.writeTextMessage(info);
        response.end();
    }
}
