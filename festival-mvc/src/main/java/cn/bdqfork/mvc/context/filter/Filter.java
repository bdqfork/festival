package cn.bdqfork.mvc.context.filter;

import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * @author bdq
 * @since 2020/1/28
 */
@FunctionalInterface
public interface Filter {

    void doFilter(RoutingContext routingContext, FilterChain filterChain);

}
