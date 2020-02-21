package cn.bdqfork.kotlin.web.route.message

import io.vertx.ext.web.RoutingContext
import java.lang.reflect.Parameter

/**
 * @author bdq
 * @since 2020/1/31
 */
interface HttpMessageHandler {
    @Throws(Exception::class)
    fun handle(routingContext: RoutingContext, parameters: Array<Parameter>): Array<Any?>
}