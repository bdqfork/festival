package cn.bdqfork.mvc.context.filter;

import cn.bdqfork.mvc.context.RouteAttribute;
import cn.bdqfork.mvc.context.handler.RouteMappingHandler;
import cn.bdqfork.security.annotation.PermitAllowed;
import cn.bdqfork.security.annotation.RolesAllowed;
import cn.bdqfork.security.util.SecurityUtils;
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
public class AuthFilter implements Filter {
    private Handler<RoutingContext> deniedHandler;

    @Override
    public void doFilter(RoutingContext routingContext, FilterChain filterChain) {
        RouteAttribute routeAttribute = (RouteAttribute) routingContext.data()
                .get(RouteMappingHandler.ROUTE_ATTRIBETE_KEY);
        if (routeAttribute == null || !routeAttribute.requireAuth()) {
            filterChain.doFilter(routingContext);
            return;
        }

        User user = routingContext.user();

        PermitAllowed permitAllowed = routeAttribute.getPermits();

        Observable<Boolean> permitObservable;
        if (permitAllowed != null) {
            permitObservable = SecurityUtils.isPermited(user, permitAllowed.value(), permitAllowed.logic());
        } else {
            permitObservable = Observable.just(true);
        }

        RolesAllowed rolesAllowed = routeAttribute.getRoles();
        Observable<Boolean> rolesObservable;
        if (rolesAllowed != null) {
            rolesObservable = SecurityUtils.isPermited(user, rolesAllowed.value(), rolesAllowed.logic());
        } else {
            rolesObservable = Observable.just(true);
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
}
