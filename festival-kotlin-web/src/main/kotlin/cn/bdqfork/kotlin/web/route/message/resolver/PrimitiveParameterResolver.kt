package cn.bdqfork.kotlin.web.route.message.resolver

import cn.bdqfork.core.util.AnnotationUtils
import cn.bdqfork.core.util.ReflectUtils
import cn.bdqfork.core.util.StringUtils
import cn.bdqfork.kotlin.web.route.annotation.Param
import io.vertx.ext.web.RoutingContext
import java.lang.reflect.Parameter
import java.util.*

/**
 * @author bdq
 * @since 2020/2/11
 */
class PrimitiveParameterResolver : AbstractParameterResolver() {

    @Throws(Exception::class)
    override fun doResolve(parameter: Parameter, routingContext: RoutingContext): Any? {
        if (!AnnotationUtils.isAnnotationPresent(parameter, Param::class.java)) {
            return null
        }
        val param = AnnotationUtils.getMergedAnnotation(parameter, Param::class.java)
        val name: String = Objects.requireNonNull(param).value
        val parameterType = parameter.type
        val params = resolveParams(routingContext)
        if (params.contains(name)) {
            return StringUtils.castToPrimitive(params[name], parameterType)
        }
        check(!param.required) {
            String.format("%s %s param %s is required but not received !",
                    routingContext.request().method(),
                    routingContext.request().path(), name)
        }
        return if ("null" == param.defaultValue || StringUtils.isEmpty(param.defaultValue)) {
            null
        } else StringUtils.castToPrimitive(param.defaultValue, parameterType)
    }

    override fun resolvable(parameter: Parameter, routingContext: RoutingContext): Boolean {
        return ReflectUtils.isPrimitiveOrWrapper(parameter.type)
    }

}