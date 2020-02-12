package cn.bdqfork.web.server;

import io.vertx.core.Promise;
import io.vertx.reactivex.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Slf4j
public class WebVerticle extends AbstractVerticle {
    private WebServer webServer;

    public WebVerticle(WebServer webServer) {
        this.webServer = webServer;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        webServer.start();
        startPromise.complete();
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        webServer.stop();
        stopPromise.complete();
    }

}
