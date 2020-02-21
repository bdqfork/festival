package cn.bdqfork.kotlin.web.route.filter

import cn.bdqfork.core.factory.processor.OrderAware
import cn.bdqfork.kotlin.web.route.RouteAttribute
import cn.bdqfork.kotlin.web.route.RouteManager
import cn.bdqfork.kotlin.web.util.SecurityUtils.isPermited
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

/**
 * @author bdq
 * @since 2020/1/28
 */
class AuthFilter : Filter, OrderAware {
    private var deniedHandler: Handler<RoutingContext>? = null

    @Throws(Exception::class)
    override fun doFilter(routingContext: RoutingContext, filterChain: FilterChain) {
        val routeAttribute = routingContext.data()[RouteManager.ROUTE_ATTRIBETE_KEY] as RouteAttribute?

        if (routeAttribute == null || !routeAttribute.isAuth || routeAttribute.isPermitAll) {
            filterChain.doFilter(routingContext)
            return
        }

        val user = routingContext.user()

        var permitResult = true
        val permitAllowed = routeAttribute.permitAllowed
        if (permitAllowed != null) {
            permitResult = isPermited(user, permitAllowed.permits, permitAllowed.logicType)
        }

        var roleResult = true
        val rolesAllowed = routeAttribute.rolesAllowed
        if (rolesAllowed != null) {
            roleResult = isPermited(user, rolesAllowed.permits, rolesAllowed.logicType)
        }

        if (permitResult && roleResult) {
            filterChain.doFilter(routingContext)
        } else if (deniedHandler != null) {
            deniedHandler!!.handle(routingContext)
        } else {
            if (log.isTraceEnabled) {
                log.trace("do default permit denied handler!")
            }
            routingContext.response().setStatusCode(403).end("permisson denied!")
        }
    }

    fun setDeniedHandler(deniedHandler: Handler<RoutingContext>?) {
        this.deniedHandler = deniedHandler
    }

    override fun getOrder(): Int {
        return 0
    }

    companion object {
        private val log = LoggerFactory.getLogger(AuthFilter::class.java)
    }
}