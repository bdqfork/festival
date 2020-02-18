package cn.bdqfork.web.server;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.web.RouterAware;
import cn.bdqfork.web.VertxAware;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * @author bdq
 * @since 2020/2/12
 */
public abstract class AbstractWebServer implements WebServer, RouterAware, VertxAware {
    protected Vertx vertx;
    protected Router router;
    private boolean running;

    @Override
    public void start() throws Exception {

        registerCoreHandler(router);

        registerOptionHandler(router);

        registerRouteMapping(router);

        doStart();
    }

    protected abstract void registerRouteMapping(Router router) throws Exception;

    protected abstract void registerOptionHandler(Router router) throws Exception;

    protected abstract void registerCoreHandler(Router router) throws Exception;

    protected abstract void doStart() throws Exception;


    @Override
    public void stop() throws Exception {
        if (running) {
            doStop();
            running = false;
        }
    }

    protected abstract void doStop() throws Exception;

    public boolean isRunning() {
        return running;
    }

    @Override
    public void setRouter(Router router) throws BeansException {
        this.router = router;
    }

    @Override
    public void setVertx(Vertx vertx) throws BeansException {
        this.vertx = vertx;
    }
}
