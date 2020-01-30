package cn.bdqfork.web.context.handler;

import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.web.context.RouteAttribute;
import cn.bdqfork.web.context.annotation.RouteMapping;
import cn.bdqfork.web.context.filter.Filter;
import cn.bdqfork.web.context.filter.FilterChain;
import io.reactivex.Observable;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Route;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author bdq
 * @since 2020/1/24
 */
@Slf4j
public class DefaultMappingHandler implements RouteMappingHandler {
    private List<Filter> filters = new LinkedList<>();
    private ResultHandler resultHandler = new DefaultResultHandler();
    protected Vertx vertx;

    public DefaultMappingHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(RouteAttribute routeAttribute) {
        Method routeMethod = routeAttribute.getRouteMethod();
        RouteMapping routeMapping = AnnotationUtils.getMergedAnnotation(routeMethod, RouteMapping.class);
        String path = routeAttribute.getBaseUrl() + Objects.requireNonNull(routeMapping).value();
        if (log.isInfoEnabled()) {
            log.info("{} mapping path:{} to {}:{}!", routeMapping.method().name(), path, routeMethod.getDeclaringClass()
                    .getCanonicalName(), ReflectUtils.getSignature(routeMethod));
        }
        Router router = routeAttribute.getRouter();

        Route route = router.route(routeMapping.method(), path);

        if (routeAttribute.requireAuth()) {
            route.handler(routeAttribute.getAuthHandler());
        }
        route.handler(routingContext -> {
            routingContext.data().put(ROUTE_ATTRIBETE_KEY, routeAttribute);
            FilterChain filterChain = new FilterChain() {
                @Override
                public void doFilter(RoutingContext routingContext) {
                    Observable.fromArray(routingContext)
                            .map(context -> invokeRouteMethod(routingContext, routeAttribute, routeMethod))
                            .subscribe(optional -> {
                                if (optional.isPresent()) {
                                    resultHandler.handle(routingContext, optional.get());
                                }
                            }, e -> {
                                log.error(e.getMessage(), e);
                                routingContext.fail(500, e);
                            });
                }
            };
            filterChain = buildFilterChain(filterChain);
            filterChain.doFilter(routingContext);
        });
    }

    private Optional<Object> invokeRouteMethod(RoutingContext routingContext, RouteAttribute routeAttribute, Method routeMethod) throws InvocationTargetException, IllegalAccessException {
        return Optional.ofNullable(ReflectUtils.invokeMethod(routeAttribute.getBean(), routeMethod, routingContext));
    }

    protected FilterChain buildFilterChain(FilterChain filterChain) {
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
