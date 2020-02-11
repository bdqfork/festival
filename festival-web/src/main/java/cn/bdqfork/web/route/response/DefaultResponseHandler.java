package cn.bdqfork.web.route.response;

import cn.bdqfork.core.util.ReflectUtils;
import io.reactivex.Observable;
import io.vertx.core.json.Json;
import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * @author bdq
 * @since 2020/1/30
 */
public class DefaultResponseHandler implements ResponseHandler {
    @Override
    public void handle(RoutingContext routingContext, Object result) {
        if (result == null) {
            return;
        }
        if (ReflectUtils.isPrimitiveOrWrapper(result.getClass())) {
            routingContext.response().end(result.toString());
            return;
        }
        if (result instanceof Observable) {
            Observable<?> observable = (Observable<?>) result;
            observable.subscribe(res -> {
                String jsonResult = Json.encodePrettily(result);
                routingContext.response().end(jsonResult);
            });
        }
        String jsonResult = Json.encodePrettily(result);
        routingContext.response().end(jsonResult);
    }
}
