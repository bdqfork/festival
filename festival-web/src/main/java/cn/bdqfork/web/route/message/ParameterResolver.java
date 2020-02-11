package cn.bdqfork.web.route.message;

import io.vertx.reactivex.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @since 2020/2/11
 */
public interface ParameterResolver {
    Object resolve(Parameter parameter, RoutingContext routingContext);
}
