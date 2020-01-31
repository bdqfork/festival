package cn.bdqfork.web.context.handler;

import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.core.MultiMap;
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
        if (httpMethod == HttpMethod.GET) {
            MultiMap multiMap = httpServerRequest.params();
            return handleQueryParams(multiMap, parameters);
        } else if (httpMethod == HttpMethod.POST || httpMethod == HttpMethod.PUT || httpMethod == HttpMethod.DELETE) {
            MultiMap multiMap = httpServerRequest.formAttributes();
            return handleFormAttributes(multiMap, parameters);
        }
        return new Object[0];
    }

    protected abstract Object[] handleQueryParams(MultiMap multiMap, Parameter[] parameters);

    protected abstract Object[] handleFormAttributes(MultiMap multiMap, Parameter[] parameters);

}
