package cn.bdqfork.kotlin.web.route.message.resolver

import cn.bdqfork.core.util.AnnotationUtils
import cn.bdqfork.kotlin.web.constant.ContentType
import cn.bdqfork.kotlin.web.route.annotation.RequestBody
import cn.bdqfork.kotlin.web.util.XmlUtils.fromXml
import io.vertx.ext.web.RoutingContext
import java.lang.reflect.Parameter

/**
 * @author bdq
 * @since 2020/2/20
 */
class XmlBodyParameterResolver : AbstractParameterResolver() {
    @Throws(Exception::class)
    override fun doResolve(parameter: Parameter, routingContext: RoutingContext): Any? {
        val parameterType = parameter.type
        if (parameterType == String::class.java) {
            return routingContext.bodyAsString
        }
        val xml = routingContext.bodyAsString
        return fromXml(xml, parameterType)
    }

    override fun resolvable(parameter: Parameter, routingContext: RoutingContext): Boolean {
        val contentType = routingContext.request().getHeader(ContentType.CONTENT_TYPE)
        return AnnotationUtils.isAnnotationPresent(parameter, RequestBody::class.java)
                && ContentType.XML == contentType
    }
}