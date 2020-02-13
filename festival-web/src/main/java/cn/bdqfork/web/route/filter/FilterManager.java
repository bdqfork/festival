package cn.bdqfork.web.route.filter;

import io.vertx.reactivex.ext.web.RoutingContext;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @since 2020/2/12
 */
public class FilterManager {
    private final List<Filter> filters = new LinkedList<>();

    public void registerFilter(Filter filter) {
        filters.add(filter);
    }

    public void registerFilters(Collection<Filter> filters) {
        this.filters.addAll(filters);
    }

    public FilterChain buildFilterChain(FilterChain target) {
        for (Filter filter : filters) {
            FilterChain nextFilterChain = target;
            target = new FilterChain() {
                @Override
                public void doFilter(RoutingContext routingContext) {
                    filter.doFilter(routingContext, nextFilterChain);
                }
            };
        }
        return target;
    }
}
