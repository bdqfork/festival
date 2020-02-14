package cn.bdqfork.web.route;

import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.web.route.filter.Filter;
import cn.bdqfork.web.route.filter.FilterChain;
import cn.bdqfork.web.route.filter.FilterChainFactory;
import cn.bdqfork.web.route.message.HttpMessageHandler;
import cn.bdqfork.web.route.response.GenericResponseHandler;
import cn.bdqfork.web.route.response.ResponseHandleStrategy;
import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Route;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.AuthHandler;
import io.vertx.reactivex.ext.web.handler.TimeoutHandler;
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

    private Router router;

    private HttpMessageHandler httpMessageHandler;

    private FilterChainFactory filterChainFactory;

    private AuthHandler authHandler;

    public RouteManager(Router router) {
        this.router = router;
    }

    public void handle(RouteAttribute routeAttribute) {
        handle(routeAttribute, null);
    }

    public void handle(RouteAttribute routeAttribute, RouteInvocation invocation) {
        String path = routeAttribute.getUrl();
        HttpMethod httpMethod = routeAttribute.getHttpMethod();

        checkIfConflict(path, httpMethod);

        Route route = router.route(httpMethod, path);

        if (routeAttribute.getTimeout() > 0) {
            route.handler(TimeoutHandler.create(routeAttribute.getTimeout()));
        }

        checkAndSetContentType(routeAttribute, route);

        checkAndSetAuth(routeAttribute, route);

        if (invocation == null) {
            if (log.isInfoEnabled()) {
                log.info("custom {} mapping path:{}!", httpMethod.name(), path);
            }

            Filter invoker = (routingContext, filterChain) -> routeAttribute.getContextHandler().handle(routingContext);
            handleMapping(routeAttribute, invoker, route);
        } else {
            if (log.isInfoEnabled()) {
                log.info("{} mapping path:{} to {}:{}!", httpMethod.name(), path,
                        invocation.method.getDeclaringClass().getCanonicalName(),
                        ReflectUtils.getSignature(invocation.method));
            }

            Filter invoker = createInvoke(invocation.bean, invocation.method);
            handleMapping(routeAttribute, invoker, route);
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

    private Filter createInvoke(Object routeBean, Method routeMethod) {
        return new Filter() {
            @Override
            public void doFilter(RoutingContext routingContext, FilterChain filterChain) throws Exception {
                Object[] args = httpMessageHandler.handle(routingContext, routeMethod.getParameters());
                Object result = ReflectUtils.invokeMethod(routeBean, routeMethod, args);
                if (ReflectUtils.isReturnVoid(routeMethod)) {
                    return;
                }
                String contentType = routingContext.getAcceptableContentType();
                HttpServerResponse response = routingContext.response();
                responseHandleStrategy.handle(response, contentType, result);
            }
        };
    }


    private void handleMapping(RouteAttribute routeAttribute, Filter invoker, Route route) {

        route.handler(routingContext -> {
            routingContext.data().put(ROUTE_ATTRIBETE_KEY, routeAttribute);
            try {
                filterChainFactory.getFilterChain(invoker)
                        .doFilter(routingContext);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                routingContext.fail(500, e);
            }
        });

    }

    public void setHttpMessageHandler(HttpMessageHandler httpMessageHandler) {
        this.httpMessageHandler = httpMessageHandler;
    }

    public void setFilterChainFactory(FilterChainFactory filterChainFactory) {
        this.filterChainFactory = filterChainFactory;
    }

    public void setAuthHandler(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }
}
