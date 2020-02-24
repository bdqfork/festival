package cn.bdqfork.kotlin.web.route.response

import cn.bdqfork.kotlin.web.util.XmlUtils
import io.vertx.ext.web.RoutingContext

/**
 * @author bdq
 * @since 2020/2/20
 */
class XmlResponseHandler : AbstractResponseHandler() {
    @Throws(Exception::class)
    override fun doHandle(routingContext: RoutingContext, result: Any) {
        routingContext.response().end(XmlUtils.toXml(result))
    }
}