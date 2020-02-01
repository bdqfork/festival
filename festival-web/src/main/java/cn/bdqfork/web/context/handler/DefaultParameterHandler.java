package cn.bdqfork.web.context.handler;

import cn.bdqfork.web.context.annotation.Param;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bdq
 * @since 2020/1/31
 */
public class DefaultParameterHandler extends AbstractParameterHandler {

    @Override
    protected Object[] doHandle(RoutingContext routingContext, Parameter[] parameters) {
        Map<String, String> rawParameters = resolveRequestAsMap(routingContext);
        Object[] res = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getType() == RoutingContext.class) {
                res[i] = routingContext;
            } else {
                String parsedArg = null;
                try {
                    parsedArg = parseArg(rawParameters, parameters[i]);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
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
                        } else {
                            res[i] = null;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
        return res;
    }

    private Map<String, String> resolveRequestAsMap(RoutingContext routingContext) {
        Map<String, String> res = new HashMap<>();
        for (Map.Entry<String, String> entry: routingContext.queryParams()) {
            res.put(entry.getKey(), entry.getValue());
        }
        return res;
    }

    private String parseArg(Map<String, String> rawParameters, Parameter parameter) throws Exception {

        if (parameter.isAnnotationPresent(Param.class)) {
            Param param = parameter.getAnnotation(Param.class);
            if (param.type() != Object.class) {
                return injectByType(rawParameters, param);
            } else {
                return injectByName(rawParameters, param);
            }
        }
        throw new Exception("parse failed");
    }

    private static String injectByType(Map<String, String> rawParameters, Param param) throws Exception {
        Class<?> type = param.type();
        String defaultValue = param.defaultValue();
        if (type == int.class || type == Integer.class || type == long.class || type == Long.class) {
            for (Map.Entry<String, String> entry: rawParameters.entrySet()){
                if (isInteger(entry.getValue())) {
                    return entry.getValue();
                }
            }
            return defaultValue;
        }
        if (type == float.class || type == Float.class || type == double.class || type == Double.class) {
            for (Map.Entry<String, String> entry: rawParameters.entrySet()){
                if (isFloat(entry.getValue())) {
                    return entry.getValue();
                }
            }
            return defaultValue;
        }

        if (type == String.class) {
            for (Map.Entry<String, String> entry: rawParameters.entrySet()){
                if (!(isInteger(entry.getValue()) && isFloat(entry.getValue()))) {
                    return entry.getValue();
                }
            }
            return defaultValue;
        }
        return defaultValue;
    }

    private static String injectByName(Map<String, String> rawParameter, Param param) {
        return rawParameter.get(param.value());
    }

    private static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    private static boolean isFloat(String str) {
        Pattern pattern = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

}
