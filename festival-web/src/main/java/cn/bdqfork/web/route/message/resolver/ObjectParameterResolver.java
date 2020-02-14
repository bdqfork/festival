package cn.bdqfork.web.route.message.resolver;

import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.core.util.StringUtils;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @since 2020/2/11
 */
public class ObjectParameterResolver extends AbstractParameterResolver {
    @Override
    protected Object doResolve(Parameter parameter, RoutingContext routingContext) {
        Class<?> parameterType = parameter.getType();
        if (!ReflectUtils.isPrimitiveOrWrapper(parameterType)) {
            MultiMap params = resolveParams(routingContext);
            return castToObject(params, parameterType);
        }
        return null;
    }

    @Override
    protected boolean resolvable(Parameter parameter) {
        return !ReflectUtils.isJavaClass(parameter.getType());
    }

    private <T> T castToObject(MultiMap params, Class<T> type) {
        T parameterObject;
        try {
            parameterObject = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

        Field[] fields = parameterObject.getClass().getDeclaredFields();

        for (Field field : fields) {

            String fieldStr = params.get(field.getName());

            Class<?> fieldType = field.getType();

            if (fieldType == String.class) {
                ReflectUtils.setValue(parameterObject, field, fieldStr);
                continue;
            }

            Object fieldValue = StringUtils.castToPrimitive(fieldStr, field.getType());

            ReflectUtils.setValue(parameterObject, field, fieldValue);
        }
        return parameterObject;
    }

}
