package cn.bdqfork.mvc.context.handler;

import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.mvc.context.MappingAttribute;
import cn.bdqfork.mvc.context.annotation.RouteMapping;
import cn.bdqfork.mvc.context.filter.Filter;
import cn.bdqfork.mvc.context.filter.FilterChain;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Route;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author bdq
 * @since 2020/1/24
 */
@Slf4j
public class DefaultMappingHandler implements RouterMappingHandler {
    private List<Filter> filters = new LinkedList<>();
    protected Vertx vertx;

    public DefaultMappingHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(MappingAttribute mappingAttribute) {
        Method routeMethod = mappingAttribute.getRouteMethod();
        RouteMapping routeMapping = AnnotationUtils.getMergedAnnotation(routeMethod, RouteMapping.class);
        assert routeMapping != null;
        String path = mappingAttribute.getBaseUrl() + routeMapping.value();
        if (log.isInfoEnabled()) {
            log.info("post mapping path:{} to {}:{}!", path, routeMethod.getDeclaringClass()
                    .getCanonicalName(), ReflectUtils.getSignature(routeMethod));
        }
        Router router = mappingAttribute.getRouter();

        Route route = router.route(routeMapping.method(), path);

        if (mappingAttribute.requireAuth()) {
            route.handler(mappingAttribute.getAuthHandler());
        }
        route.handler(routingContext -> {
            FilterChain filterChain = new FilterChain() {
                @Override
                public void doFilter(RoutingContext routingContext) {
                    try {
                        ReflectUtils.invokeMethod(mappingAttribute.getBean(), routeMethod, routingContext);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
            };
            filterChain = buildFilterChain(filterChain);
            filterChain.doFilter(routingContext);
        });
    }

    protected FilterChain buildFilterChain(FilterChain filterChain) {
        Collections.reverse(filters);
        for (Filter filter : filters) {
            FilterChain finalFilterChain = filterChain;
            filterChain = new FilterChain() {
                @Override
                public void doFilter(RoutingContext routingContext) {
                    filter.doFilter(routingContext, finalFilterChain);
                }
            };
        }
        return filterChain;
    }

    public void registerFilter(Filter filter) {
        filters.add(filter);
    }

}
