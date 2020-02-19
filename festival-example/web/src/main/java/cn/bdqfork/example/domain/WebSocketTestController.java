package cn.bdqfork.example.domain;

import cn.bdqfork.web.route.annotation.OnActive;
import cn.bdqfork.web.route.annotation.OnClose;
import cn.bdqfork.web.route.annotation.OnOpen;
import cn.bdqfork.web.route.annotation.ServerEndpoint;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author bdq
 * @since 2020/2/19
 */
@ServerEndpoint("/test")
@Singleton
@Named
public class WebSocketTestController {
    private static Logger log = LoggerFactory.getLogger(WebSocketTestController.class);

    @OnOpen
    public void open(ServerWebSocket serverWebSocket) {
        log.info("open......");
    }

    @OnActive
    public void active(WebSocketFrame webSocketFrame) {
        log.info("active......");
    }

    @OnClose
    public void close() {
        log.info("close......");
    }
}
