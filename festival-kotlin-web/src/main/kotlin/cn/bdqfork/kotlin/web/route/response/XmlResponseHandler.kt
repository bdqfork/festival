package cn.bdqfork.kotlin.web.route.response

import cn.bdqfork.kotlin.web.util.XmlUtils
import io.vertx.core.http.HttpServerResponse

/**
 * @author bdq
 * @since 2020/2/20
 */
class XmlResponseHandler : AbstractResponseHandler() {
    @Throws(Exception::class)
    override fun doHandle(httpServerResponse: HttpServerResponse, result: Any) {
        httpServerResponse.end(XmlUtils.toXml(result))
    }
}