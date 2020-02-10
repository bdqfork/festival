package cn.bdqfork.web.route.handler;

import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.web.annotation.Param;
import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author bdq
 * @since 2020/1/31
 */
public class DefaultParameterHandler extends AbstractParameterHandler {

    @Override
    protected Object[] doHandle(RoutingContext routingContext, Parameter[] parameters) {
        MultiMap params = resolveParams(routingContext);

        List<Object> args = new ArrayList<>(parameters.length);

        for (Parameter parameter : parameters) {

            Class<?> parameterType = parameter.getType();

            if (parameterType == RoutingContext.class) {
                args.add(routingContext);
                continue;
            }
            if (parameterType == HttpServerRequest.class) {
                args.add(routingContext.request());
                continue;
            }
            if (parameterType == HttpServerResponse.class) {
                args.add(routingContext.response());
                continue;
            }

            if (parameterType == Map.class) {
                Map<String, String> paramsMap = new HashMap<>();
                for (Map.Entry<String, String> entry :
                        params) {
                    paramsMap.put(entry.getKey(), entry.getValue());
                }
                args.add(paramsMap);
                continue;
            }


            if (!ReflectUtils.isPrimitiveOrWrapper(parameterType)) {
                try {
                    args.add(castToObject(params, parameterType));
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new IllegalStateException(String.format("new instance of %s failed!", parameterType.getName()));
                }
                continue;
            }

            if (!AnnotationUtils.isAnnotationPresent(parameter, Param.class)) {
                continue;
            }

            Param param = AnnotationUtils.getMergedAnnotation(parameter, Param.class);

            String name = Objects.requireNonNull(param).value();

            String paramValue = params.get(name);

            if (params.contains(name)) {

                args.add(castToPrimitive(paramValue, parameterType));

            } else if (param.required()) {

                throw new IllegalStateException(String.format("%s %s param %s is required but not received !",
                        routingContext.request().method(),
                        routingContext.request().path(), name));

            } else {

                args.add(castToPrimitive(param.defaultValue(), parameterType));

            }

        }
        return args.toArray();
    }

    private <T> T castToObject(MultiMap params, Class<T> type) throws IllegalAccessException, InstantiationException {
        T parameterObject = type.newInstance();
        Field[] fields = parameterObject.getClass().getDeclaredFields();
        for (Field field: fields) {
            String fieldStr = params.get(field.getName());
            Class<?> fieldType = field.getType();
            if (fieldType == String.class) {
                ReflectUtils.setValue(parameterObject, field, fieldStr);
                continue;
            }
            Object fieldValue = castToPrimitive(fieldStr, field.getType());
            ReflectUtils.setValue(parameterObject, field, fieldValue);
        }
        return parameterObject;
    }

    private Object castToPrimitive(String value, Class<?> type) {
        if (type == Integer.class || type == int.class) {
            return Integer.valueOf(value);
        }

        if (type == Long.class || type == long.class) {
            return Long.valueOf(value);
        }

        if (type == Double.class || type == double.class) {
            return Double.valueOf(value);
        }

        if (type == Float.class || type == float.class) {
            return Float.valueOf(value);
        }

        if (type == Short.class || type == short.class) {
            return Short.parseShort(value);
        }

        if (type == Byte.class || type == byte.class) {
            return Byte.parseByte(value);
        }

        if (type == Character.class || type == char.class) {
            return value.toCharArray()[0];
        }

        if (type == Boolean.class || type == boolean.class) {
            return Boolean.valueOf(value);
        }

        throw new IllegalArgumentException(String.format("unsupport type %s!", type.getCanonicalName()));
    }

    private MultiMap resolveParams(RoutingContext routingContext) {
        if (routingContext.request().method() == HttpMethod.GET) {
            return routingContext.queryParams();
        } else {
            HttpServerRequest httpServerRequest = routingContext.request();
            return httpServerRequest.formAttributes();
        }
    }

}
