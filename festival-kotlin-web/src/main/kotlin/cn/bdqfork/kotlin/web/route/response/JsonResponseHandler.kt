package cn.bdqfork.web.route.response

import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json

/**
 * @author bdq
 * @since 2020/2/12
 */
class JsonResponseHandler : AbstractResponseHandler() {
    @Throws(Exception::class)
    override fun doHandle(httpServerResponse: HttpServerResponse, result: Any) {
        httpServerResponse.end(Json.encodePrettily(result))
    }
}