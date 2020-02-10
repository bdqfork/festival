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

    public void registerFilter(Filter filter) {
        filters.add(filter);
    }

    public void handle(RouteAttribute routeAttribute) {
        handle(routeAttribute, null);
    }

    public void handle(RouteAttribute routeAttribute, RouteInvocationHolder invocationHolder) {

        String path = routeAttribute.getUrl();
        HttpMethod httpMethod = routeAttribute.getHttpMethod();

        String signature = generateRouteSignature(httpMethod, path);

        if (registedRoutes.contains(signature)) {
            throw new IllegalStateException(String.format("conflict mapping %s !", signature));
        } else {
            registedRoutes.add(signature);
        }

        Route route = router.route(httpMethod, path);

        if (!routeAttribute.isPermitAll()) {
            route.handler(authHandler);
        }

        if (invocationHolder == null) {
            if (log.isInfoEnabled()) {
                log.info("custom {} mapping path:{}!", httpMethod.name(), path);
            }
            route.handler(routeAttribute.getContextHandler());
            return;
        }

        if (log.isInfoEnabled()) {
            log.info("{} mapping path:{} to {}:{}!", httpMethod.name(), path,
                    invocationHolder.method.getDeclaringClass().getCanonicalName(),
                    ReflectUtils.getSignature(invocationHolder.method));
        }

        FilterChain invoker = createInvokeHandler(invocationHolder.bean, invocationHolder.method);

        FilterChain filterChain = buildFilterChain(invoker);

        route.handler(routingContext -> {
            routingContext.data().put(ROUTE_ATTRIBETE_KEY, routeAttribute);
            filterChain.doFilter(routingContext);
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

    private FilterChain createInvokeHandler(Object routeBean, Method routeMethod) {
        return new FilterChain() {
            @Override
            public void doFilter(RoutingContext routingContext) {
                Observable.fromArray(routingContext.request())
                        .map(request -> parameterHandler.handle(routingContext, routeMethod.getParameters()))
                        .map(args -> invokeRouteMethod(routeBean, routeMethod, args))
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

    public static class RouteInvocationHolder {
        Object bean;
        Method method;

        public RouteInvocationHolder(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }
    }

}
