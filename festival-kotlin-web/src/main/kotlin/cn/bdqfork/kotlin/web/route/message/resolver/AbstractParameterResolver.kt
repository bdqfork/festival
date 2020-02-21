package cn.bdqfork.kotlin.web.route.message.resolver

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

    @Throws(Exception::class)
    override fun resolve(parameter: Parameter, routingContext: RoutingContext): Any? {
        if (resolvable(parameter,routingContext)) {
            return doResolve(parameter, routingContext)
        }
        return if (next != null) {
            next!!.resolve(parameter, routingContext)
        } else null
    }

    @Throws(Exception::class)
    protected abstract fun doResolve(parameter: Parameter, routingContext: RoutingContext): Any?
    protected abstract fun resolvable(parameter: Parameter,routingContext: RoutingContext): Boolean

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