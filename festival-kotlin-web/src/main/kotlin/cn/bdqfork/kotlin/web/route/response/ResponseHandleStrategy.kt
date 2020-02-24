package cn.bdqfork.kotlin.web.route.response

import io.vertx.ext.web.RoutingContext

/**
 * @author bdq
 * @since 2020/1/30
 */
interface ResponseHandleStrategy {
    @Throws(Exception::class)
    fun handle(routingContext: RoutingContext, result: Any?)
}