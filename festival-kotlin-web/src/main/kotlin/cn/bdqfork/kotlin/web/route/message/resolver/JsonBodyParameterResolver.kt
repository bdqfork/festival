package cn.bdqfork.kotlin.web.route.message.resolver

import cn.bdqfork.core.util.AnnotationUtils
import cn.bdqfork.kotlin.web.route.annotation.RequestBody
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import java.lang.reflect.Parameter

/**
 * @author bdq
 * @since 2020/2/13
 */
class JsonBodyParameterResolver : AbstractParameterResolver() {
    override fun doResolve(parameter: Parameter, routingContext: RoutingContext): Any? {
        val parameterType = parameter.type
        if (parameterType == JsonObject::class.java) {
            return routingContext.bodyAsJson
        }
        if (parameterType == JsonArray::class.java) {
            return routingContext.bodyAsJsonArray
        }
        if (parameterType == String::class.java) {
            return routingContext.bodyAsString
        }
        val buffer = routingContext.body
        return Json.decodeValue(buffer, parameterType)
    }

    override fun resolvable(parameter: Parameter): Boolean {
        return AnnotationUtils.isAnnotationPresent(parameter, RequestBody::class.java)
    }
}