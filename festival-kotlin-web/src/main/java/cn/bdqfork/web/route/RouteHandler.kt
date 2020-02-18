package cn.bdqfork.web.route

import cn.bdqfork.core.util.ReflectUtils
import cn.bdqfork.web.route.message.HttpMessageHandler
import cn.bdqfork.web.route.response.ResponseHandlerFactory
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import java.lang.reflect.Method

/**
 * @author bdq
 * @since 2020/2/18
 */
class RouteHandler(private val httpMessageHandler: HttpMessageHandler, private val responseHandlerFactory: ResponseHandlerFactory, private val method: Method, private val bean: Any) : Handler<RoutingContext> {
    override fun handle(routingContext: RoutingContext) {
        try {
            val args = httpMessageHandler.handle(routingContext, method.parameters)
            val result = ReflectUtils.invokeMethod(bean, method, *args)
            if (ReflectUtils.isReturnVoid(method)) {
                return
            }
            val contentType = routingContext.acceptableContentType
            val response = routingContext.response()
            responseHandlerFactory.getResponseHandler(contentType).handle(response, result)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

}