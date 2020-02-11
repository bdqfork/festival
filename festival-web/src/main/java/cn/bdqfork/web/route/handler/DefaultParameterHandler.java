package cn.bdqfork.web.route.handler;

import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.core.util.StringUtils;
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

            Object value = resovleContextParamValue(routingContext, parameterType);
            if (value != null) {
                args.add(value);
                continue;
            }

            value = resolveObjectParamValue(params, parameterType);

            if (value != null) {
                args.add(value);
                continue;
            }

            value = resolvePrimitiveParamValue(parameter, routingContext);

            args.add(value);

        }
        return args.toArray();
    }

    private Object resolveObjectParamValue(MultiMap params, Class<?> parameterType) {
        if (!ReflectUtils.isPrimitiveOrWrapper(parameterType)) {
            return castToObject(params, parameterType);
        }
        return null;
    }

    private Object resolvePrimitiveParamValue(Parameter parameter, RoutingContext routingContext) {

        if (!ReflectUtils.isPrimitiveOrWrapper(parameter.getType())) {
            return null;
        }

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

    private Object resovleContextParamValue(RoutingContext routingContext, Class<?> parameterType) {
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

    private MultiMap resolveParams(RoutingContext routingContext) {
        if (routingContext.request().method() == HttpMethod.GET) {
            return routingContext.queryParams();
        } else {
            HttpServerRequest httpServerRequest = routingContext.request();
            return httpServerRequest.formAttributes();
        }
    }

}
