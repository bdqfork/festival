package cn.bdqfork.web.route.message.resolver;

import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.web.route.annotation.Param;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * @author bdq
 * @since 2020/2/11
 */
public class PrimitiveParameterResolver extends AbstractParameterResolver {
    @Override
    protected Object doResolve(Parameter parameter, RoutingContext routingContext) {
        if (!AnnotationUtils.isAnnotationPresent(parameter, Param.class)) {
            return null;
        }

        Param param = AnnotationUtils.getMergedAnnotation(parameter, Param.class);

        String name = Objects.requireNonNull(param).value();

        Class<?> parameterType = parameter.getType();

        MultiMap params = resolveParams(routingContext);

        if (params.contains(name)) {
            return StringUtils.castToPrimitive(params.get(name), parameterType);
        }

        if (param.required()) {
            throw new IllegalStateException(String.format("%s %s param %s is required but not received !",
                    routingContext.request().method(),
                    routingContext.request().path(), name));

        }

        if ("null".equals(param.defaultValue()) || StringUtils.isEmpty(param.defaultValue())) {
            return null;
        }

        return StringUtils.castToPrimitive(param.defaultValue(), parameterType);
    }

    @Override
    protected boolean resolvable(Parameter parameter) {
        return ReflectUtils.isPrimitiveOrWrapper(parameter.getType());
    }

}
