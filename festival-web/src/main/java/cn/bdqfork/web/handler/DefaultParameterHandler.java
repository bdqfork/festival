package cn.bdqfork.web.handler;

import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @since 2020/1/31
 */
public class DefaultParameterHandler extends AbstractParameterHandler {

    @Override
    protected Object[] doHandle(RoutingContext routingContext, Parameter[] parameters) {
        HttpServerRequest httpServerRequest = routingContext.request();
        MultiMap multiMap = httpServerRequest.formAttributes();
        return new Object[0];
    }
}
