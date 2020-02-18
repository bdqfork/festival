package cn.bdqfork.web.route.response

import io.vertx.core.http.HttpServerResponse

/**
 * @author bdq
 * @since 2020/2/12
 */
class TextPlainResponseHandler : AbstractResponseHandler() {
    @Throws(Exception::class)
    override fun doHandle(httpServerResponse: HttpServerResponse, result: Any) {
        httpServerResponse.end(result.toString())
    }
}