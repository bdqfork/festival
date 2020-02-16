package cn.bdqfork.web.route.message.resolver;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * @author bdq
 * @since 2020/2/11
 */
public abstract class AbstractParameterResolver implements ParameterResolver {

    private AbstractParameterResolver next;

    public void setNext(AbstractParameterResolver next) {
        this.next = next;
    }

    @Override
    public Object resolve(Parameter parameter, RoutingContext routingContext) {
        if (resolvable(parameter)) {
            return doResolve(parameter, routingContext);
        }
        if (next != null) {
            return next.resolve(parameter, routingContext);
        }
        return null;
    }

    protected abstract Object doResolve(Parameter parameter, RoutingContext routingContext);

    protected abstract boolean resolvable(Parameter parameter);

    protected MultiMap resolveParams(RoutingContext routingContext) {
        Map<String, String> pathParams = routingContext.pathParams();
        if (routingContext.request().method() == HttpMethod.GET) {
            return routingContext.queryParams().addAll(pathParams);
        } else {
            HttpServerRequest httpServerRequest = routingContext.request();
            return httpServerRequest.formAttributes().addAll(pathParams);
        }
    }
}
