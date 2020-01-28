package cn.bdqfork.mvc.context.filter;

import cn.bdqfork.mvc.context.SecuritySystemManager;
import cn.bdqfork.mvc.context.MappingAttribute;
import cn.bdqfork.security.annotation.PermitAllowed;
import cn.bdqfork.security.annotation.RolesAllowed;
import cn.bdqfork.security.util.SecurityUtils;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.vertx.reactivex.ext.auth.User;
import io.vertx.reactivex.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bdq
 * @since 2020/1/28
 */
@Slf4j
public class AuthFilter implements Filter {
    private SecuritySystemManager securitySystemManager;
    private MappingAttribute mappingAttribute;

    public AuthFilter(SecuritySystemManager securitySystemManager, MappingAttribute mappingAttribute) {
        this.securitySystemManager = securitySystemManager;
        this.mappingAttribute = mappingAttribute;
    }

    @Override
    public void doFilter(RoutingContext routingContext, FilterChain filterChain) {
        if (mappingAttribute.requireAuth()) {
            filterChain.doFilter(routingContext);
            return;
        }
        User user = routingContext.user();

        PermitAllowed permitAllowed = mappingAttribute.getPermits();

        Observable<Boolean> permitObservable;
        if (permitAllowed != null) {
            permitObservable = SecurityUtils.isPermited(user, permitAllowed.value(), permitAllowed.logic());
        } else {
            permitObservable = Observable.just(true);
        }

        RolesAllowed rolesAllowed = mappingAttribute.getRoles();
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
                                   } else {
                                       securitySystemManager.getPermitDeniedHandler().handle(routingContext);
                                   }
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                if (log.isErrorEnabled()) {
                                    log.error(e.getMessage(), e);
                                }
                                routingContext.response().setStatusCode(500).end(e.getMessage());
                            }
                        });
    }
}
