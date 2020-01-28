package cn.bdqfork.mvc.mapping.filter;

import cn.bdqfork.mvc.mapping.MappingAttribute;
import cn.bdqfork.security.annotation.PermitAllowed;
import cn.bdqfork.security.annotation.RolesAllowed;
import cn.bdqfork.security.util.SecurityUtils;
import io.reactivex.Observable;
import io.vertx.reactivex.ext.auth.User;
import io.vertx.reactivex.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bdq
 * @since 2020/1/28
 */
@Slf4j
public class AuthFilter implements Filter {
    private MappingAttribute mappingAttribute;

    public AuthFilter(MappingAttribute mappingAttribute) {
        this.mappingAttribute = mappingAttribute;
    }

    @Override
    public void doFilter(RoutingContext routingContext, FilterChain filterChain) {
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

        Observable.combineLatest(permitObservable, rolesObservable, (res1, res2) -> res1 && res2)
                .subscribe(res -> {
                            if (res) {
                                filterChain.doFilter(routingContext);
                            } else {
                                routingContext.response().setStatusCode(500).end("permisson denied!");
                            }
                        },
                        e -> {
                            if (log.isErrorEnabled()) {
                                log.error(e.getMessage(), e);
                            }
                            routingContext.response().setStatusCode(500).end(e.getMessage());
                        });
    }
}
