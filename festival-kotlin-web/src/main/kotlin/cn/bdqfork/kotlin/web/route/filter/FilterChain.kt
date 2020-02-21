package cn.bdqfork.kotlin.web.route.filter

import io.vertx.ext.web.RoutingContext
import java.util.*

/**
 * @author bdq
 * @since 2020/1/28
 */
class FilterChain(filters: List<Filter>) : Filter {
    private val filters: MutableList<Filter> = ArrayList()
    private var index = 0

    init {
        this.filters.addAll(filters)
    }

    fun registerFilter(filter: Filter) {
        filters.add(filter)
    }

    @Throws(Exception::class)
    fun doFilter(routingContext: RoutingContext) {
        doFilter(routingContext, this)
    }

    @Throws(Exception::class)
    override fun doFilter(routingContext: RoutingContext, filterChain: FilterChain) {
        if (index == filters.size) {
            return
        }
        filters[index++].doFilter(routingContext, filterChain)
    }
}