package cn.bdqfork.web;

import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.web.filter.Filter;
import cn.bdqfork.web.filter.FilterChain;
import cn.bdqfork.web.handler.DefaultParameterHandler;
import cn.bdqfork.web.handler.DefaultResultHandler;
import cn.bdqfork.web.handler.ParameterHandler;
import cn.bdqfork.web.handler.ResultHandler;
import io.reactivex.Observable;
import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.ext.web.Route;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.AuthHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2020/2/10
 */
@Slf4j
public class RouteManager {
    public static final String ROUTE_ATTRIBETE_KEY = "routeAttribute";

    private final Set<String> registedRoutes = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private List<Filter> filters = new LinkedList<>();

    private ResultHandler resultHandler = new DefaultResultHandler();

    private ParameterHandler parameterHandler = new DefaultParameterHandler();

    private Router router;

    private AuthHandler authHandler;

    public RouteManager(Router router, AuthHandler authHandler) {
        this.router = router;
        this.authHandler = authHandler;
    }

    public void handle(RouteAttribute routeAttribute) {
        Method routeMethod = routeAttribute.getRouteMethod();

        String path = routeAttribute.getBaseUrl() + routeAttribute.getUrl();
        HttpMethod httpMethod = routeAttribute.getHttpMethod();

        String signature = generateRouteSignature(httpMethod, path);

        if (registedRoutes.contains(signature)) {
            throw new IllegalStateException(String.format("conflict mapping %s !", signature));
        } else {
            registedRoutes.add(signature);
        }

        if (log.isInfoEnabled()) {
            log.info("{} mapping path:{} to {}:{}!", httpMethod.name(), path, routeMethod.getDeclaringClass()
                    .getCanonicalName(), ReflectUtils.getSignature(routeMethod));
        }

        Route route = router.route(httpMethod, path);

        if (!routeAttribute.isPermitAll()) {
            route.handler(authHandler);
        }

        route.handler(routingContext -> {
            routingContext.data().put(ROUTE_ATTRIBETE_KEY, routeAttribute);
            buildFilterChain(routeAttribute).doFilter(routingContext);
        });
    }

    private String generateRouteSignature(HttpMethod httpMethod, String path) {
        return httpMethod.name() + ":" + path;
    }

    private Optional<Object> invokeRouteMethod(Object routeBean, Method routeMethod, Object[] args) {
        try {
            return Optional.ofNullable(ReflectUtils.invokeMethod(routeBean, routeMethod, args));
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    protected FilterChain buildFilterChain(RouteAttribute routeAttribute) {
        FilterChain filterChain = createInvokeHandler(routeAttribute);

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

    private FilterChain createInvokeHandler(RouteAttribute routeAttribute) {
        return new FilterChain() {
            @Override
            public void doFilter(RoutingContext routingContext) {
                Observable.fromArray(routingContext.request())
                        .map(request -> {
                            Method routeMethod = routeAttribute.getRouteMethod();
                            return parameterHandler.handle(routingContext, routeMethod.getParameters());
                        })
                        .map(args -> {
                            Object routeBean = routeAttribute.getBean();
                            Method routeMethod = routeAttribute.getRouteMethod();
                            return invokeRouteMethod(routeBean, routeMethod, args);
                        })
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
    }

    public void registerFilter(Filter filter) {
        filters.add(filter);
    }
}
