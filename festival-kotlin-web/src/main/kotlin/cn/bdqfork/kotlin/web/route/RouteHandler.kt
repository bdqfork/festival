package cn.bdqfork.kotlin.web.route

import cn.bdqfork.core.util.ReflectUtils
import cn.bdqfork.kotlin.web.constant.ContentType
import cn.bdqfork.kotlin.web.route.message.HttpMessageHandler
import cn.bdqfork.kotlin.web.route.response.ResponseHandlerFactory
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
            var contentType = routingContext.acceptableContentType
            if (result is ModelAndView) {
                contentType = ContentType.HTML
            }
            responseHandlerFactory.getResponseHandler(contentType).handle(routingContext, result)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

}