package cn.bdqfork.web.route;

import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.web.route.filter.Filter;
import cn.bdqfork.web.route.filter.FilterChain;
import cn.bdqfork.web.route.message.DefaultHttpMessageHandler;
import cn.bdqfork.web.route.message.HttpMessageHandler;
import cn.bdqfork.web.route.response.DefaultResponseHandler;
import cn.bdqfork.web.route.response.ResponseHandler;
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

    private ResponseHandler responseHandler = new DefaultResponseHandler();

    private HttpMessageHandler httpMessageHandler = new DefaultHttpMessageHandler();

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

    public void handle(RouteAttribute routeAttribute, RouteInvocation invocation) {

        String path = routeAttribute.getUrl();
        HttpMethod httpMethod = routeAttribute.getHttpMethod();

        String signature = generateRouteSignature(httpMethod, path);

        if (registedRoutes.contains(signature)) {
            throw new IllegalStateException(String.format("conflict mapping %s !", signature));
        } else {
            registedRoutes.add(signature);
        }

        Route route = router.route(httpMethod, path);

        if (authHandler != null && routeAttribute.isAuth() && !routeAttribute.isPermitAll()) {
            route.handler(authHandler);
        }

        if (invocation == null) {
            if (log.isInfoEnabled()) {
                log.info("custom {} mapping path:{}!", httpMethod.name(), path);
            }

            FilterChain invoker = new FilterChain() {
                @Override
                public void doFilter(RoutingContext routingContext) {
                    routeAttribute.getContextHandler().handle(routingContext);
                }
            };

            FilterChain filterChain = buildFilterChain(invoker);

            doHandler(routeAttribute, route, filterChain);

        } else {
            if (log.isInfoEnabled()) {
                log.info("{} mapping path:{} to {}:{}!", httpMethod.name(), path,
                        invocation.method.getDeclaringClass().getCanonicalName(),
                        ReflectUtils.getSignature(invocation.method));
            }

            FilterChain invoker = createInvokeHandler(invocation.bean, invocation.method);

            FilterChain filterChain = buildFilterChain(invoker);

            doHandler(routeAttribute, route, filterChain);
        }

    }

    private void doHandler(RouteAttribute routeAttribute, Route route, FilterChain filterChain) {
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
                        .map(request -> httpMessageHandler.handle(routingContext, routeMethod.getParameters()))
                        .map(args -> invokeRouteMethod(routeBean, routeMethod, args))
                        .subscribe(optional -> {
                            if (optional.isPresent()) {
                                responseHandler.handle(routingContext, optional.get());
                            }
                        }, e -> {
                            log.error(e.getMessage(), e);
                            routingContext.fail(500, e);
                        });
            }
        };
    }

}
