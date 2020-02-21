package cn.bdqfork.kotlin.web.route.message.resolver

import io.vertx.core.MultiMap
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.Session
import java.lang.reflect.Parameter

/**
 * @author bdq
 * @since 2020/2/11
 */
class ContextParameterResolver : AbstractParameterResolver() {

    @Throws(Exception::class)
    override fun doResolve(parameter: Parameter, routingContext: RoutingContext): Any? {
        val parameterType = parameter.type
        if (parameterType == RoutingContext::class.java) {
            return routingContext
        }
        if (parameterType == HttpServerRequest::class.java) {
            return routingContext.request()
        }
        if (parameterType == HttpServerResponse::class.java) {
            return routingContext.response()
        }
        if (parameterType == Session::class.java) {
            return routingContext.session()
        }
        if (parameterType == MultiMap::class.java) {
            return resolveParams(routingContext)
        }
        if (parameterType == JsonObject::class.java) {
            val jsonObject = routingContext.bodyAsJson
            return jsonObject ?: JsonObject()
        }
        return null
    }

    override fun resolvable(parameter: Parameter, routingContext: RoutingContext): Boolean {
        val parameterType = parameter.type
        return parameterType == RoutingContext::class.java ||
                parameterType == HttpServerRequest::class.java ||
                parameterType == HttpServerResponse::class.java ||
                parameterType == MultiMap::class.java ||
                parameterType == JsonObject::class.java
    }

}