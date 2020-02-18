package cn.bdqfork.web.route.response

import io.vertx.core.http.HttpServerResponse

/**
 * @author bdq
 * @since 2020/2/12
 */
abstract class AbstractResponseHandler : ResponseHandleStrategy {
    @Throws(Exception::class)
    override fun handle(httpServerResponse: HttpServerResponse, result: Any?) {
        if (result == null) {
            httpServerResponse.end()
        }
        doHandle(httpServerResponse, result!!)
    }

    @Throws(Exception::class)
    protected abstract fun doHandle(httpServerResponse: HttpServerResponse, result: Any)
}