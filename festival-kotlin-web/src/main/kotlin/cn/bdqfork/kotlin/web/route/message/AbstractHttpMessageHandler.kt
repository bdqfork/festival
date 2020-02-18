package cn.bdqfork.web.route.message

import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext
import java.lang.reflect.Parameter

/**
 * @author bdq
 * @since 2020/1/31
 */
abstract class AbstractHttpMessageHandler : HttpMessageHandler {
    override fun handle(routingContext: RoutingContext, parameters: Array<Parameter>): Array<Any?> {
        val httpServerRequest = routingContext.request()
        val httpMethod = httpServerRequest.method()
        return if (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.POST || httpMethod == HttpMethod.PUT || httpMethod == HttpMethod.DELETE) {
            doHandle(routingContext, parameters)
        } else arrayOfNulls(0)
    }

    protected abstract fun doHandle(routingContext: RoutingContext, parameters: Array<Parameter>): Array<Any?>
}