package cn.bdqfork.web.route;

import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.web.route.filter.FilterChain;
import cn.bdqfork.web.route.filter.FilterChainFactory;
import cn.bdqfork.web.route.message.DefaultHttpMessageHandler;
import cn.bdqfork.web.route.message.HttpMessageHandler;
import cn.bdqfork.web.route.response.GenericResponseHandler;
import cn.bdqfork.web.route.response.ResponseHandleStrategy;
import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Route;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.AuthHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2020/2/10
 */
@Slf4j
public class RouteManager {
    public static final String ROUTE_ATTRIBETE_KEY = "routeAttribute";

    private final Set<String> registedRoutes = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private ResponseHandleStrategy responseHandleStrategy = new GenericResponseHandler();

    private HttpMessageHandler httpMessageHandler = new DefaultHttpMessageHandler();

    private FilterChainFactory filterChainFactory;

    private Router router;

    private AuthHandler authHandler;

    public RouteManager(Router router, FilterChainFactory filterChainFactory) {
        this.router = router;
        this.filterChainFactory = filterChainFactory;
    }

    public RouteManager(Router router, FilterChainFactory filterChainFactory, AuthHandler authHandler) {
        this.router = router;
        this.filterChainFactory = filterChainFactory;
        this.authHandler = authHandler;
    }

    public void handle(RouteAttribute routeAttribute) {
        handle(routeAttribute, null);
    }

    public void handle(RouteAttribute routeAttribute, RouteInvocation invocation) {
        String path = routeAttribute.getUrl();
        HttpMethod httpMethod = routeAttribute.getHttpMethod();

        checkIfConflict(path, httpMethod);

        Route route = router.route(httpMethod, path);

        checkAndSetContentType(routeAttribute, route);

        checkAndSetAuth(routeAttribute, route);

        if (invocation == null) {
            if (log.isInfoEnabled()) {
                log.info("custom {} mapping path:{}!", httpMethod.name(), path);
            }

            handleCustomMapping(routeAttribute, route);
        } else {
            if (log.isInfoEnabled()) {
                log.info("{} mapping path:{} to {}:{}!", httpMethod.name(), path,
                        invocation.method.getDeclaringClass().getCanonicalName(),
                        ReflectUtils.getSignature(invocation.method));
            }

            handleMapping(routeAttribute, invocation, route);
        }

    }

    private void checkAndSetContentType(RouteAttribute routeAttribute, Route route) {
        if (!StringUtils.isEmpty(routeAttribute.getConsumes())) {
            route.consumes(routeAttribute.getConsumes());
        }

        if (!StringUtils.isEmpty(routeAttribute.getProduces())) {
            route.produces(routeAttribute.getProduces());
        }
    }

    private void checkIfConflict(String path, HttpMethod httpMethod) {
        String signature = generateRouteSignature(httpMethod, path);

        if (registedRoutes.contains(signature)) {
            throw new IllegalStateException(String.format("conflict mapping %s !", signature));
        } else {
            registedRoutes.add(signature);
        }
    }

    private String generateRouteSignature(HttpMethod httpMethod, String path) {
        return httpMethod.name() + ":" + path;
    }

    private void checkAndSetAuth(RouteAttribute routeAttribute, Route route) {
        if (authHandler != null && routeAttribute.isAuth() && !routeAttribute.isPermitAll()) {
            route.handler(authHandler);
        }
    }

    private void handleCustomMapping(RouteAttribute routeAttribute, Route route) {
        FilterChain invoker = new FilterChain() {
            @Override
            public void doFilter(RoutingContext routingContext) {
                routeAttribute.getContextHandler().handle(routingContext);
            }
        };

        FilterChain filterChain = filterChainFactory.getFilterChain(invoker);

        doHandler(routeAttribute, route, filterChain);
    }

    private void handleMapping(RouteAttribute routeAttribute, RouteInvocation invocation, Route route) {
        FilterChain invoker = createInvokeHandler(invocation.bean, invocation.method);

        FilterChain filterChain = filterChainFactory.getFilterChain(invoker);

        doHandler(routeAttribute, route, filterChain);
    }

    private void doHandler(RouteAttribute routeAttribute, Route route, FilterChain filterChain) {
        route.handler(routingContext -> {
            routingContext.data().put(ROUTE_ATTRIBETE_KEY, routeAttribute);
            filterChain.doFilter(routingContext);
        });
    }

    private FilterChain createInvokeHandler(Object routeBean, Method routeMethod) {
        return new FilterChain() {
            @Override
            public void doFilter(RoutingContext routingContext) {
                try {
                    Object[] args = httpMessageHandler.handle(routingContext, routeMethod.getParameters());
                    Object result = ReflectUtils.invokeMethod(routeBean, routeMethod, args);
                    String contentType = routingContext.getAcceptableContentType();
                    HttpServerResponse response = routingContext.response();
                    responseHandleStrategy.handle(response, contentType, result);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    routingContext.fail(500, e);
                }
            }
        };
    }

}
