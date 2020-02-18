package cn.bdqfork.web.route.filter

import io.vertx.ext.web.RoutingContext

/**
 * @author bdq
 * @since 2020/1/28
 */
@FunctionalInterface
interface Filter {
    @Throws(Exception::class)
    fun doFilter(routingContext: RoutingContext, filterChain: FilterChain)
}