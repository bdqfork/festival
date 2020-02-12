package cn.bdqfork.web.route.message;

import io.vertx.reactivex.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @since 2020/1/31
 */
public interface HttpMessageHandler {
    Object[] handle(RoutingContext routingContext, Parameter[] parameters) throws Exception;
}
