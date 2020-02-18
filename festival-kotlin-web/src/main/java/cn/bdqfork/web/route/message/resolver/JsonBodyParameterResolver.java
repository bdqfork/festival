package cn.bdqfork.web.route.message.resolver;

import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.web.route.annotation.RequestBody;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @since 2020/2/13
 */
public class JsonBodyParameterResolver extends AbstractParameterResolver {
    @Override
    protected Object doResolve(Parameter parameter, RoutingContext routingContext) {
        Class<?> parameterType = parameter.getType();
        if (parameterType == JsonObject.class) {
            return routingContext.getBodyAsJson();
        }
        if (parameterType == JsonArray.class) {
            return routingContext.getBodyAsJsonArray();
        }
        if (parameterType == String.class) {
            return routingContext.getBodyAsString();
        }
        Buffer buffer = routingContext.getBody();
        return Json.decodeValue(buffer, parameterType);
    }

    @Override
    protected boolean resolvable(Parameter parameter) {
        return AnnotationUtils.isAnnotationPresent(parameter, RequestBody.class);
    }
}
