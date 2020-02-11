package cn.bdqfork.web.route.message;

import io.vertx.reactivex.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @since 2020/2/11
 */
public class GenericParameterResolver implements ParameterResolver {
    private AbstractParameterResolver parameterResolver;

    public GenericParameterResolver() {
        PrimitiveParameterResolver primitiveParameterResolver = new PrimitiveParameterResolver();

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
