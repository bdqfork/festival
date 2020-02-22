package cn.bdqfork.kotlin.web.route.response

import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext

/**
 * @author bdq
 * @since 2020/2/12
 */
class JsonResponseHandler : AbstractResponseHandler() {
    @Throws(Exception::class)
    override fun doHandle(routingContext: RoutingContext, result: Any) {
        routingContext.response().end(Json.encodePrettily(result))
    }
}