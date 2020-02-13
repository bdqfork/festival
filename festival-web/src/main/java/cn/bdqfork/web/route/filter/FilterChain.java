package cn.bdqfork.web.route.filter;

import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * @author bdq
 * @since 2020/1/28
 */
@FunctionalInterface
public interface FilterChain {

    void doFilter(RoutingContext routingContext);

}
