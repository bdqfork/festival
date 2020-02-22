package cn.bdqfork.kotlin.web.route.response

import io.vertx.ext.web.RoutingContext

/**
 * @author bdq
 * @since 2020/2/12
 */
abstract class AbstractResponseHandler : ResponseHandleStrategy {
    @Throws(Exception::class)
    override fun handle(routingContext: RoutingContext, result: Any?) {
        if (result == null) {
            routingContext.response().end()
        }
        doHandle(routingContext, result!!)
    }

    @Throws(Exception::class)
    protected abstract fun doHandle(routingContext: RoutingContext, result: Any)
}