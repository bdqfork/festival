package cn.bdqfork.web;

import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.AopUtils;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.web.annotation.RouteMapping;
import cn.bdqfork.web.filter.Filter;
import cn.bdqfork.web.filter.FilterChain;
import cn.bdqfork.web.handler.DefaultParameterHandler;
import cn.bdqfork.web.handler.DefaultResultHandler;
import cn.bdqfork.web.handler.ParameterHandler;
import cn.bdqfork.web.handler.ResultHandler;
import io.reactivex.Observable;
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
 * @since 2020/1/29
 */
@Slf4j
public class RouteHandler {
    public static final String ROUTE_ATTRIBETE_KEY = "routeAttribute";

    private final Set<String> registedRoutes = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private List<Filter> filters = new LinkedList<>();

    private ResultHandler resultHandler = new DefaultResultHandler();

    private ParameterHandler parameterHandler = new DefaultParameterHandler();

    private Router router;

    private AuthHandler authHandler;

    public RouteHandler(Router router, AuthHandler authHandler) {
        this.router = router;
        this.authHandler = authHandler;
    }

    public void resolve(List<?> beans) {
        List<RouteAttribute> attributes = new LinkedList<>();

        for (Object routeBean : beans) {

            Class<?> beanClass = AopUtils.getTargetClass(routeBean);

            String baseUrl = resolveBaseUrl(beanClass);

            for (Method declaredMethod : beanClass.getDeclaredMethods()) {

                if (!AnnotationUtils.isAnnotationPresent(declaredMethod, RouteMapping.class)) {
                    continue;
                }

                RouteAttribute attribute = buildRouteAttribute(routeBean, baseUrl, declaredMethod);

                attributes.add(attribute);
            }
        }

        attributes.forEach(this::handle);
    }

    private RouteAttribute buildRouteAttribute(Object routeBean, String baseUrl, Method routeMethod) {
        return RouteAttribute.builder()
                .setRouteMethod(routeMethod)
                .setBean(routeBean)
                .setBaseUrl(baseUrl)
                .setAuthHandler(authHandler)
                .build();
    }

    private String resolveBaseUrl(Class<?> beanClass) {
        if (AnnotationUtils.isAnnotationPresent(beanClass, RouteMapping.class)) {
            return Objects.requireNonNull(AnnotationUtils.getMergedAnnotation(beanClass, RouteMapping.class))
                    .value();
        }
        return "";
    }

    private void handle(RouteAttribute routeAttribute) {
        Method routeMethod = routeAttribute.getRouteMethod();

        RouteMapping routeMapping = AnnotationUtils.getMergedAnnotation(routeMethod, RouteMapping.class);
        String path = routeAttribute.getBaseUrl() + Objects.requireNonNull(routeMapping).value();

        String signature = generateRouteSignature(routeMapping, path);
        if (registedRoutes.contains(signature)) {
            throw new IllegalStateException(String.format("conflict mapping %s !", signature));
        } else {
            registedRoutes.add(signature);
        }

        if (log.isInfoEnabled()) {
            log.info("{} mapping path:{} to {}:{}!", routeMapping.method().name(), path, routeMethod.getDeclaringClass()
                    .getCanonicalName(), ReflectUtils.getSignature(routeMethod));
        }

        Route route = router.route(routeMapping.method(), path);

        if (routeAttribute.requireAuth()) {
            route.handler(routeAttribute.getAuthHandler());
        }

        route.handler(routingContext -> {
            routingContext.data().put(ROUTE_ATTRIBETE_KEY, routeAttribute);
            buildFilterChain(routeAttribute, routeMethod).doFilter(routingContext);
        });
    }

    private String generateRouteSignature(RouteMapping routeMapping, String path) {
        return routeMapping.method().name() + ":" + path;
    }

    private Optional<Object> invokeRouteMethod(Object routeBean, Method routeMethod, Object[] args) throws InvocationTargetException, IllegalAccessException {
        return Optional.ofNullable(ReflectUtils.invokeMethod(routeBean, routeMethod, args));
    }

    protected FilterChain buildFilterChain(RouteAttribute routeAttribute, Method routeMethod) {
        FilterChain filterChain = new FilterChain() {
            @Override
            public void doFilter(RoutingContext routingContext) {
                Observable.fromArray(routingContext.request())
                        .map(request -> parameterHandler.handle(routingContext, routeMethod.getParameters()))
                        .map(args -> invokeRouteMethod(routeAttribute.getBean(), routeMethod, args))
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
