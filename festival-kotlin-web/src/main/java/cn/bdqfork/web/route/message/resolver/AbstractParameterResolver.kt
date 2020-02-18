package cn.bdqfork.web.route.message.resolver

import io.vertx.core.MultiMap
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext
import java.lang.reflect.Parameter

/**
 * @author bdq
 * @since 2020/2/11
 */
abstract class AbstractParameterResolver : ParameterResolver {
    private var next: AbstractParameterResolver? = null

    fun setNext(next: AbstractParameterResolver?) {
        this.next = next
    }

    override fun resolve(parameter: Parameter, routingContext: RoutingContext): Any? {
        if (resolvable(parameter)) {
            return doResolve(parameter, routingContext)
        }
        return if (next != null) {
            next!!.resolve(parameter, routingContext)
        } else null
    }

    protected abstract fun doResolve(parameter: Parameter, routingContext: RoutingContext): Any?
    protected abstract fun resolvable(parameter: Parameter): Boolean

    protected fun resolveParams(routingContext: RoutingContext): MultiMap {
        val pathParams = routingContext.pathParams()
        return if (routingContext.request().method() == HttpMethod.GET) {
            routingContext.queryParams().addAll(pathParams)
        } else {
            val httpServerRequest = routingContext.request()
            httpServerRequest.formAttributes().addAll(pathParams)
        }
    }
}