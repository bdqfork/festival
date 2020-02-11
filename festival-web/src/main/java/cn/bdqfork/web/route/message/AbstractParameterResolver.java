package cn.bdqfork.web.route.message;

import io.vertx.reactivex.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

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
}
