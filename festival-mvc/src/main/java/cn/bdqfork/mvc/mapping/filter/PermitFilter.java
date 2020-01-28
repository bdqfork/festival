package cn.bdqfork.mvc.mapping.filter;

import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.mvc.mapping.MappingAttribute;
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
        if (StringUtils.isEmpty(mappingAttribute.getPermits())) {
            filterChain.doFilter(routingContext);
            return;
        }
        User user = routingContext.user();
        user.rxIsAuthorized(mappingAttribute.getPermits())
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
