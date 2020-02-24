package cn.bdqfork.web.route.response;


import io.vertx.ext.web.RoutingContext;

/**
 * @author bdq
 * @since 2020/1/30
 */
public interface ResponseHandleStrategy {

    void handle(RoutingContext routingContext, Object result) throws Exception;
}
