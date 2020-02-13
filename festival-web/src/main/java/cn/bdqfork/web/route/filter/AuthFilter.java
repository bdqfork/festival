package cn.bdqfork.web.route.filter;

import cn.bdqfork.core.factory.processor.OrderAware;
import cn.bdqfork.web.route.PermitHolder;
import cn.bdqfork.web.route.RouteAttribute;
import cn.bdqfork.web.route.RouteManager;
import cn.bdqfork.web.util.SecurityUtils;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.auth.User;
import io.vertx.reactivex.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bdq
 * @since 2020/1/28
 */
@Slf4j
public class AuthFilter implements Filter, OrderAware {
    private Handler<RoutingContext> deniedHandler;

    @Override
    public void doFilter(RoutingContext routingContext, FilterChain filterChain) {
        RouteAttribute routeAttribute = (RouteAttribute) routingContext.data()
                .get(RouteManager.ROUTE_ATTRIBETE_KEY);
        if (routeAttribute == null || !routeAttribute.isAuth() || routeAttribute.isPermitAll()) {
            filterChain.doFilter(routingContext);
            return;
        }

        User user = routingContext.user();

        PermitHolder permitAllowed = routeAttribute.getPermitAllowed();

        Observable<Boolean> permitObservable = Observable.just(true);

        if (permitAllowed != null) {
            permitObservable = SecurityUtils.isPermited(user, permitAllowed.getPermits(), permitAllowed.getLogicType());
        }

        PermitHolder rolesAllowed = routeAttribute.getRolesAllowed();

        Observable<Boolean> rolesObservable = Observable.just(true);

        if (rolesAllowed != null) {
            rolesObservable = SecurityUtils.isPermited(user, rolesAllowed.getPermits(), rolesAllowed.getLogicType());
        }

        Observable.combineLatest(permitObservable, rolesObservable,
                new BiFunction<Boolean, Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean res1, Boolean res2) throws Exception {
                        return res1 && res2;
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean res) throws Exception {
                                   if (res) {
                                       filterChain.doFilter(routingContext);
                                       return;
                                   }
                                   if (deniedHandler != null) {
                                       deniedHandler.handle(routingContext);
                                   } else {
                                       if (log.isTraceEnabled()) {
                                           log.trace("do default permit denied handler!");
                                       }
                                       routingContext.response().setStatusCode(403).end("permisson denied!");
                                   }
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                if (log.isErrorEnabled()) {
                                    log.error(e.getMessage(), e);
                                }
                                routingContext.fail(500, e);
                            }
                        });
    }

    public void setDeniedHandler(Handler<RoutingContext> deniedHandler) {
        this.deniedHandler = deniedHandler;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
