package cn.bdqfork.web.route.message;

import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @since 2020/2/11
 */
public class ContextParameterResolver extends AbstractParameterResolver {
    @Override
    protected Object doResolve(Parameter parameter, RoutingContext routingContext) {
        Class<?> parameterType = parameter.getType();

        if (parameterType == RoutingContext.class) {
            return routingContext;
        }
        if (parameterType == HttpServerRequest.class) {
            return routingContext.request();
        }
        if (parameterType == HttpServerResponse.class) {
            return routingContext.response();
        }

        if (parameterType == MultiMap.class) {
            return resolveParams(routingContext);
        }

        return null;
    }

    @Override
    protected boolean resolvable(Parameter parameter) {
        Class<?> parameterType = parameter.getType();
        return parameterType == RoutingContext.class ||
                parameterType == HttpServerRequest.class ||
                parameterType == HttpServerResponse.class ||
                parameterType == MultiMap.class;

    }

}
