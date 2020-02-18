package cn.bdqfork.kotlin.web.route.response

import io.vertx.core.http.HttpServerResponse

/**
 * @author bdq
 * @since 2020/1/30
 */
interface ResponseHandleStrategy {
    @Throws(Exception::class)
    fun handle(httpServerResponse: HttpServerResponse, result: Any?)
}