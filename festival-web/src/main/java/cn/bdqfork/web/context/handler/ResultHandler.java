package cn.bdqfork.web.context.handler;

import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * @author bdq
 * @since 2020/1/30
 */
public interface ResultHandler {
    void handle(RoutingContext routingContext, Object result) throws Exception;
}
