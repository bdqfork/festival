package cn.bdqfork.web.route.handler;

import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @since 2020/1/31
 */
public abstract class AbstractParameterHandler implements ParameterHandler {
    @Override
    public Object[] handle(RoutingContext routingContext, Parameter[] parameters) {
        HttpServerRequest httpServerRequest = routingContext.request();
        HttpMethod httpMethod = httpServerRequest.method();
        if (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.POST
                || httpMethod == HttpMethod.PUT || httpMethod == HttpMethod.DELETE) {
            return doHandle(routingContext, parameters);
        }
        return new Object[0];
    }

    protected abstract Object[] doHandle(RoutingContext routingContext, Parameter[] parameters);

}
