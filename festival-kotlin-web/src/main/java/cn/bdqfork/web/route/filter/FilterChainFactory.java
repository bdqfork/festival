package cn.bdqfork.web.route.filter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @since 2020/2/13
 */
public class FilterChainFactory {
    private final List<Filter> filters = new LinkedList<>();

    public void registerFilter(Filter filter) {
        filters.add(filter);
    }

    public void registerFilters(Collection<Filter> filters) {
        this.filters.addAll(filters);
    }

    public FilterChain getFilterChain(Filter target) {
        FilterChain filterChain = new FilterChain(filters);
        filterChain.registerFilter(target);
        return filterChain;
    }
}
