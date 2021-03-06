package cn.bdqfork.web.route.filter;


import io.vertx.ext.web.RoutingContext;

/**
 * @author bdq
 * @since 2020/1/28
 */
@FunctionalInterface
public interface Filter {

    void doFilter(RoutingContext routingContext, FilterChain filterChain) throws Exception;

}
