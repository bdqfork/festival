package cn.bdqfork.web.route.filter;

import cn.bdqfork.core.factory.processor.OrderAware;
import cn.bdqfork.web.route.PermitHolder;
import cn.bdqfork.web.route.RouteAttribute;
import cn.bdqfork.web.route.RouteManager;
import cn.bdqfork.web.util.SecurityUtils;
import io.vertx.core.Handler;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2020/1/28
 */
public class AuthFilter implements Filter, OrderAware {
    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);
    private Handler<RoutingContext> deniedHandler;

    @Override
    public void doFilter(RoutingContext routingContext, FilterChain filterChain) throws Exception {
        RouteAttribute routeAttribute = (RouteAttribute) routingContext.data()
                .get(RouteManager.ROUTE_ATTRIBETE_KEY);
        if (routeAttribute == null || !routeAttribute.isAuth() || routeAttribute.isPermitAll()) {
            filterChain.doFilter(routingContext);
            return;
        }

        User user = routingContext.user();
        boolean permitResult = true;
        PermitHolder permitAllowed = routeAttribute.getPermitAllowed();
        if (permitAllowed != null) {
            permitResult = SecurityUtils.isPermited(user, permitAllowed.getPermits(), permitAllowed.getLogicType());
        }

        boolean roleResult = true;
        PermitHolder rolesAllowed = routeAttribute.getRolesAllowed();
        if (rolesAllowed != null) {
            roleResult = SecurityUtils.isPermited(user, rolesAllowed.getPermits(), rolesAllowed.getLogicType());
        }

        if (permitResult && roleResult) {
            filterChain.doFilter(routingContext);
        } else if (deniedHandler != null) {
            deniedHandler.handle(routingContext);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("do default permit denied handler!");
            }
            routingContext.response().setStatusCode(403).end("permisson denied!");
        }
    }

    public void setDeniedHandler(Handler<RoutingContext> deniedHandler) {
        this.deniedHandler = deniedHandler;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
