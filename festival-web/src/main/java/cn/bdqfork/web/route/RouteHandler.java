package cn.bdqfork.web.route;

import cn.bdqfork.web.route.filter.FilterChain;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * @author bdq
 * @since 2020/2/10
 */
public class RouteHandler implements Handler<RoutingContext> {
    private RouteAttribute routeAttribute;
    private FilterChain filterChain;

    public RouteHandler(RouteAttribute routeAttribute, FilterChain filterChain) {
        this.routeAttribute = routeAttribute;
        this.filterChain = filterChain;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        routingContext.data().put(RouteManager.ROUTE_ATTRIBETE_KEY, routeAttribute);
        filterChain.doFilter(routingContext);
    }
}
