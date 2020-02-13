package cn.bdqfork.web.route.message.resolver;

import io.vertx.reactivex.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @since 2020/2/11
 */
public class GenericParameterResolver implements ParameterResolver {
    private AbstractParameterResolver parameterResolver;

    public GenericParameterResolver() {
        JsonBodyParameterResolver jsonBodyParameterResolver = new JsonBodyParameterResolver();

        PrimitiveParameterResolver primitiveParameterResolver = new PrimitiveParameterResolver();
        primitiveParameterResolver.setNext(jsonBodyParameterResolver);

        ObjectParameterResolver objectParameterResolver = new ObjectParameterResolver();
        objectParameterResolver.setNext(primitiveParameterResolver);

        ContextParameterResolver contextParameterResolver = new ContextParameterResolver();
        contextParameterResolver.setNext(objectParameterResolver);

        parameterResolver = contextParameterResolver;
    }

    @Override
    public Object resolve(Parameter parameter, RoutingContext routingContext) {
        return parameterResolver.resolve(parameter, routingContext);
    }
}
