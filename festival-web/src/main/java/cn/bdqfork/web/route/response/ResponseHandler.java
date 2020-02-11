package cn.bdqfork.web.route.response;

import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * @author bdq
 * @since 2020/1/30
 */
public interface ResponseHandler {
    void handle(RoutingContext routingContext, Object result) throws Exception;
}
