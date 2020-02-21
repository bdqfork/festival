package cn.bdqfork.web.route.response;

import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

/**
 * @author bdq
 * @since 2020/2/12
 */
public class JsonResponseHandler extends AbstractResponseHandler {

    @Override
    protected void doHandle(RoutingContext routingContext, Object result) throws Exception {
        routingContext.response().end(Json.encodePrettily(result));
    }
}
