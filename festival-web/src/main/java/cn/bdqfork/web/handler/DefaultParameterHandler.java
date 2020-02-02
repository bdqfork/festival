package cn.bdqfork.web.handler;

import cn.bdqfork.web.context.annotation.Param;
import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bdq
 * @since 2020/1/31
 */
public class DefaultParameterHandler extends AbstractParameterHandler {

    @Override
    protected Object[] doHandle(RoutingContext routingContext, Parameter[] parameters) {
        Map<String, String> rawParameters = resolveParametersAsMap(routingContext);
        Object[] res = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getType() == RoutingContext.class) {
                res[i] = routingContext;
            } else if (parameters[i].getType() == HttpServerRequest.class) {
                res[i] = routingContext.request();
            } else if (parameters[i].getType() == HttpServerResponse.class) {
                res[i] = routingContext.response();
            } else {
                String parsedArg = parseArg(rawParameters, parameters[i]);
                if (parameters[i].getClass() != null && parsedArg != null) {
                    Class<?> parameterType = parameters[i].getType();
                    try {
                        if (parameterType == String.class) {
                            res[i] = parsedArg;
                        } else if (parameterType == Integer.class || parameterType == int.class) {
                            res[i] = Integer.parseInt(parsedArg);
                        } else if (parameterType == Long.class || parameterType == long.class) {
                            res[i] = Long.parseLong(parsedArg);
                        } else if (parameterType == Double.class || parameterType == double.class) {
                            res[i] = Double.parseDouble(parsedArg);
                        } else if (parameterType == Float.class || parameterType == float.class) {
                            res[i] = Float.parseFloat(parsedArg);
                        } else if (parameterType == Short.class || parameterType == short.class) {
                            res[i] = Short.parseShort(parsedArg);
                        } else if (parameterType == Byte.class || parameterType == byte.class) {
                            res[i] = Byte.parseByte(parsedArg);
                        } else if (parameterType == Character.class || parameterType == char.class) {
                            if (parsedArg.length() == 1) {
                                res[i] = parsedArg.charAt(0);
                            }
                        } else if (parameterType == Boolean.class || parameterType == boolean.class) {
                            res[i] = Boolean.parseBoolean(parsedArg);
                        } else {
                            res[i] = null;
                        }
                    } catch (NumberFormatException e) {
                        res[i] = null;
                    }
                }
            }
        }
        return res;
    }

    private Map<String, String> resolveParametersAsMap(RoutingContext routingContext) {
        Map<String, String> res = new HashMap<>();
        if (routingContext.request().method() == HttpMethod.GET) {
            for (Map.Entry<String, String> entry : routingContext.queryParams()) {
                res.put(entry.getKey(), entry.getValue());
            }
        } else {
            HttpServerRequest httpServerRequest = routingContext.request();
            MultiMap multiMap = httpServerRequest.formAttributes();
            for (Map.Entry<String, String> entry : multiMap) {
                res.put(entry.getKey(), entry.getValue());
            }
        } 
        return res;
    }

    private String parseArg(Map<String, String> rawParameters, Parameter parameter) {
        String res = "";
        if (parameter.isAnnotationPresent(Param.class)) {
            Param param = parameter.getAnnotation(Param.class);
            res = rawParameters.get(param.value());
            if (res == null || res.isEmpty()) {
                res = param.defaultValue();
            }
        }
        return res;
    }
}
