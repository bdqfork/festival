package cn.bdqfork.web.route.message;

import cn.bdqfork.web.route.message.resolver.ParameterResolver;
import cn.bdqfork.web.route.message.resolver.ParameterResolverFactory;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bdq
 * @since 2020/1/31
 */
public class DefaultHttpMessageHandler extends AbstractHttpMessageHandler {
    private ParameterResolver parameterResolver;

    public DefaultHttpMessageHandler(ParameterResolverFactory parameterResolverFactory) {
        this.parameterResolver = parameterResolverFactory.createResolverChain();
    }

    @Override
    protected Object[] doHandle(RoutingContext routingContext, Parameter[] parameters) {

        List<Object> args = new ArrayList<>(parameters.length);

        for (Parameter parameter : parameters) {
            Object value = parameterResolver.resolve(parameter, routingContext);
            args.add(value);
        }
        return args.toArray();
    }

}
