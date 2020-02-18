package cn.bdqfork.web.route.message

import cn.bdqfork.web.route.message.resolver.ParameterResolver
import cn.bdqfork.web.route.message.resolver.ParameterResolverFactory
import io.vertx.ext.web.RoutingContext
import java.lang.reflect.Parameter
import java.util.*

/**
 * @author bdq
 * @since 2020/1/31
 */
class DefaultHttpMessageHandler(parameterResolverFactory: ParameterResolverFactory) : AbstractHttpMessageHandler() {
    private val parameterResolver: ParameterResolver = parameterResolverFactory.createResolverChain()
    override fun doHandle(routingContext: RoutingContext, parameters: Array<Parameter>): Array<Any?> {
        val args: MutableList<Any?> = ArrayList(parameters.size)
        for (parameter in parameters) {
            val value = parameterResolver.resolve(parameter, routingContext)
            args.add(value)
        }
        return args.toTypedArray()
    }

}