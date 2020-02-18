package cn.bdqfork.web.route.message.resolver

import java.util.*

/**
 * @author bdq
 * @since 2020/2/13
 */
class ParameterResolverFactory {
    private val parameterResolvers: MutableList<AbstractParameterResolver> = ArrayList()
    private fun registerResolver(parameterResolver: AbstractParameterResolver) {
        parameterResolvers.add(parameterResolver)
    }

    fun registerResolver(parameterResolvers: Collection<AbstractParameterResolver>) {
        this.parameterResolvers.addAll(parameterResolvers)
    }

    fun createResolverChain(): ParameterResolver {
        for (i in 0 until parameterResolvers.size - 1) {
            val resolver = parameterResolvers[i]
            val next = parameterResolvers[i + 1]
            resolver.setNext(next)
        }
        return parameterResolvers[0]
    }

    init {
        registerResolver(ContextParameterResolver())
        registerResolver(PrimitiveParameterResolver())
        registerResolver(JsonBodyParameterResolver())
        registerResolver(DateParameterResolver())
    }
}