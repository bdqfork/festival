package cn.bdqfork.kotlin.web.route.response

import io.vertx.ext.web.RoutingContext

/**
 * @author bdq
 * @since 2020/2/12
 */
class TextPlainResponseHandler : AbstractResponseHandler() {
    @Throws(Exception::class)
    override fun doHandle(routingContext: RoutingContext, result: Any) {
        routingContext.response().end(result.toString())
    }
}