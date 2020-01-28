package cn.bdqfork.mvc.mapping.filter;

import cn.bdqfork.mvc.mapping.MappingAttribute;
import cn.bdqfork.security.util.SecurityUtils;
import cn.bdqfork.security.annotation.PermitAllowed;
import io.vertx.reactivex.ext.auth.User;
import io.vertx.reactivex.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bdq
 * @since 2020/1/28
 */
@Slf4j
public class PermitFilter implements Filter {
    private MappingAttribute mappingAttribute;

    public PermitFilter(MappingAttribute mappingAttribute) {
        this.mappingAttribute = mappingAttribute;
    }

    @Override
    public void doFilter(RoutingContext routingContext, FilterChain filterChain) {
        PermitAllowed permitAllowed = mappingAttribute.getPermits();
        if (permitAllowed == null) {
            filterChain.doFilter(routingContext);
            return;
        }
        User user = routingContext.user();
        SecurityUtils.isPermited(user, permitAllowed.value())
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
