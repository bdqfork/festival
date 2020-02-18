package cn.bdqfork.web.route;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.util.BeanUtils;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.web.route.filter.Filter;
import cn.bdqfork.web.route.filter.FilterChainFactory;
import cn.bdqfork.web.route.message.DefaultHttpMessageHandler;
import cn.bdqfork.web.route.message.HttpMessageHandler;
import cn.bdqfork.web.route.message.resolver.AbstractParameterResolver;
import cn.bdqfork.web.route.message.resolver.ParameterResolverFactory;
import cn.bdqfork.web.route.response.ResponseHandlerFactory;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2020/2/10
 */
public class RouteManager {
    private static final Logger log = LoggerFactory.getLogger(RouteManager.class);
    public static final String ROUTE_ATTRIBETE_KEY = "routeAttribute";

    private final Set<String> registedRoutes = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private RouteResolver routeResolver;

    private ConfigurableBeanFactory beanFactory;

    private Router router;

    private HttpMessageHandler httpMessageHandler;

    private FilterChainFactory filterChainFactory;

    private ResponseHandlerFactory responseHandlerFactory;

    private AuthHandler authHandler;

    public RouteManager(ConfigurableBeanFactory beanFactory, Router router) {
        this.beanFactory = beanFactory;
        this.router = router;
        initAuthHandler();
        initFilterChainFactory();
        initHttpMessageHandler();
        initResponseHandlerFactory();
        initRouteResolver();
    }

    private void initAuthHandler() {
        try {
            authHandler = beanFactory.getBean(AuthHandler.class);
        } catch (NoSuchBeanException e) {
            if (log.isDebugEnabled()) {
                log.debug("no auth handler found!");
            }
        } catch (BeansException e) {
            throw new IllegalStateException(e);
        }
    }

    private void initFilterChainFactory() {
        List<Filter> filters = getFilters();
        filters = BeanUtils.sortByOrder(filters);

        FilterChainFactory filterChainFactory = new FilterChainFactory();
        filterChainFactory.registerFilters(filters);
        this.filterChainFactory = filterChainFactory;
    }

    private List<Filter> getFilters() {
        try {
            return new ArrayList<>(beanFactory.getBeans(Filter.class).values());
        } catch (NoSuchBeanException e) {
            if (log.isDebugEnabled()) {
                log.debug("no filter found!");
            }
            return Collections.emptyList();
        } catch (BeansException e) {
            throw new IllegalStateException(e);
        }
    }

    private void initHttpMessageHandler() {
        ParameterResolverFactory parameterResolverFactory = new ParameterResolverFactory();
        Collection<AbstractParameterResolver> parameterResolvers;
        try {
            parameterResolvers = beanFactory.getBeans(AbstractParameterResolver.class).values();
        } catch (BeansException e) {
            throw new IllegalStateException(e);
        }
        parameterResolverFactory.registerResolver(parameterResolvers);
        httpMessageHandler = new DefaultHttpMessageHandler(parameterResolverFactory);
    }

    private void initResponseHandlerFactory() {
        responseHandlerFactory = new ResponseHandlerFactory();
    }

    private void initRouteResolver() {
        routeResolver = new RouteResolver(httpMessageHandler, responseHandlerFactory);
    }

    public void registerRouteMapping() throws Exception {
        Collection<RouteAttribute> routes = routeResolver.resovleRoutes(beanFactory);
        routes.forEach(this::handle);
    }

    private void handle(RouteAttribute routeAttribute) {
        checkIfRouteConflict(routeAttribute);

        Route route = createRoute(routeAttribute);

        setTimeoutIfNeed(routeAttribute, route);

        setContentTypeIfNeed(routeAttribute, route);

        setAuthIfNeed(routeAttribute, route);

        handleMapping(routeAttribute, route);

    }

    private void checkIfRouteConflict(RouteAttribute routeAttribute) {
        String signature = generateRouteSignature(routeAttribute.getHttpMethod(), routeAttribute.getUrl());

        if (registedRoutes.contains(signature)) {
            throw new IllegalStateException(String.format("conflict mapping %s !", signature));
        } else {
            registedRoutes.add(signature);
        }
    }

    private String generateRouteSignature(HttpMethod httpMethod, String path) {
        return httpMethod.name() + ":" + path;
    }

    private Route createRoute(RouteAttribute routeAttribute) {
        return router.route(routeAttribute.getHttpMethod(), routeAttribute.getUrl());
    }

    private void setTimeoutIfNeed(RouteAttribute routeAttribute, Route route) {
        if (routeAttribute.getTimeout() > 0) {
            route.handler(TimeoutHandler.create(routeAttribute.getTimeout()));
        }
    }


    private void setContentTypeIfNeed(RouteAttribute routeAttribute, Route route) {
        if (!StringUtils.isEmpty(routeAttribute.getConsumes())) {
            route.consumes(routeAttribute.getConsumes());
        }

        if (!StringUtils.isEmpty(routeAttribute.getProduces())) {
            route.produces(routeAttribute.getProduces());
        }
    }

    private void setAuthIfNeed(RouteAttribute routeAttribute, Route route) {
        if (authHandler != null && routeAttribute.isAuth() && !routeAttribute.isPermitAll()) {
            route.handler(authHandler);
        }
    }

    private void handleMapping(RouteAttribute routeAttribute, Route route) {

        if (log.isInfoEnabled()) {
            log.info("{} mapping path:{}!", routeAttribute.getHttpMethod().name(), routeAttribute.getUrl());
        }

        Filter invoker = (routingContext, filterChain) -> routeAttribute.getContextHandler().handle(routingContext);

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

}
