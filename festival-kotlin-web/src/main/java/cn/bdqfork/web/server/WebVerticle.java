package cn.bdqfork.web.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

/**
 * @author bdq
 * @since 2020/1/21
 */
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
