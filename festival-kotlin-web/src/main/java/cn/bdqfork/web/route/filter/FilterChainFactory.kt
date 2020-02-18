package cn.bdqfork.web.route.filter

import java.util.*

/**
 * @author bdq
 * @since 2020/2/13
 */
class FilterChainFactory {
    private val filters: MutableList<Filter> = LinkedList()

    fun registerFilter(filter: Filter) {
        filters.add(filter)
    }

    fun registerFilters(filters: Collection<Filter>) {
        this.filters.addAll(filters)
    }

    fun getFilterChain(target: Filter): FilterChain {
        val filterChain = FilterChain(filters)
        filterChain.registerFilter(target)
        return filterChain
    }
}