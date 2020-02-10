package cn.bdqfork.web;

import cn.bdqfork.context.aware.BeanFactoryAware;
import cn.bdqfork.context.aware.ResourceReaderAware;
import cn.bdqfork.context.configuration.reader.ResourceReader;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.AopUtils;
import cn.bdqfork.core.util.BeanUtils;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.web.annotation.*;
import cn.bdqfork.web.constant.ApplicationProperty;
import cn.bdqfork.web.filter.AuthFilter;
import cn.bdqfork.web.filter.Filter;
import io.reactivex.Completable;
import io.vertx.core.Promise;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.auth.AuthProvider;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.AuthHandler;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.LoggerHandler;
import io.vertx.reactivex.ext.web.handler.SessionHandler;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;
import io.vertx.reactivex.ext.web.sstore.SessionStore;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Slf4j
public class WebSeverRunner extends AbstractVerticle implements BeanFactoryAware, ResourceReaderAware, RouterAware {
    private ConfigurableBeanFactory beanFactory;
    private HttpServer httpServer;
    private ResourceReader resourceReader;
    private Router router;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        registerLoggingHandler(router);

        registerSessionHandler(router);

        registerBodyHandler(router);

        registerRouteMapping(router);

        startServer(router);

        startPromise.complete();
    }

    private void registerLoggingHandler(Router router) throws BeansException {
        LoggerHandler loggerHandler;
        try {
            loggerHandler = beanFactory.getBean(LoggerHandler.class);
            router.route().handler(loggerHandler);
        } catch (NoSuchBeanException e) {
            if (log.isDebugEnabled()) {
                log.debug("no logger handler registed!");
            }
        }
    }

    private void registerSessionHandler(io.vertx.reactivex.ext.web.Router router) throws BeansException {
        SessionStore sessionStore = getOrCreateSessionStore();

        SessionHandler sessionHandler = SessionHandler.create(sessionStore);

        resolveAndSetSessionProperties(sessionHandler);

        AuthProvider authProvider = getAuthProvider();

        if (authProvider != null) {
            sessionHandler.setAuthProvider(authProvider);
        }

        router.route().handler(sessionHandler);
    }

    private SessionStore getOrCreateSessionStore() throws BeansException {
        SessionStore sessionStore;
        try {
            sessionStore = beanFactory.getBean(SessionStore.class);
        } catch (NoSuchBeanException e) {
            sessionStore = LocalSessionStore.create(vertx);
        }
        return sessionStore;
    }

    private void resolveAndSetSessionProperties(SessionHandler sessionHandler) {
        Boolean cookieHttpOnly = resourceReader.readProperty(ApplicationProperty.SERVER_COOKIE_HTTP_ONLY);
        if (cookieHttpOnly != null) {
            sessionHandler.setCookieHttpOnlyFlag(cookieHttpOnly);
        }

        Boolean cookieSecure = resourceReader.readProperty(ApplicationProperty.SERVER_COOKIE_SECURE);
        if (cookieSecure != null) {
            sessionHandler.setCookieSecureFlag(cookieSecure);
        }

        Long sessionTimeout = resourceReader.readProperty(ApplicationProperty.SERVER_SESSION_TIMEOUT);
        if (sessionTimeout != null) {
            sessionHandler.setSessionTimeout(sessionTimeout);
        }

        String sessionCookieName = resourceReader.readProperty(ApplicationProperty.SERVER_SESSION_COOKIE_NAME);
        if (!StringUtils.isEmpty(sessionCookieName)) {
            sessionHandler.setSessionCookieName(sessionCookieName);
        }

        String sessionCookiePath = resourceReader.readProperty(ApplicationProperty.SERVER_SESSION_COOKIE_PATH);
        if (!StringUtils.isEmpty(sessionCookiePath)) {
            sessionHandler.setSessionCookiePath(sessionCookiePath);
        }

    }

    private AuthProvider getAuthProvider() {
        AuthProvider authProvider = null;
        try {
            authProvider = beanFactory.getBean(AuthProvider.class);
        } catch (BeansException e) {
            if (log.isDebugEnabled()) {
                log.debug("no auth provider");
            }
        }
        return authProvider;
    }

    private void registerRouteMapping(Router router) throws Exception {
        AuthHandler authHandler = getAuthHandler();

        RouteManager routeManager = new RouteManager(router, authHandler);

        List<Filter> filters = new LinkedList<>();

        registerAuthFilterIfNeed(filters);

        filters = getOrderedFilters(filters);

        filters.forEach(routeManager::registerFilter);

        doRegisterMapping(routeManager);

        Collection<RouteAttribute> customRoutes = getCustomRoute();

        customRoutes.forEach(routeManager::handle);

    }

    private Collection<RouteAttribute> getCustomRoute() throws BeansException {
        return beanFactory.getBeans(RouteAttribute.class).values();
    }

    private void doRegisterMapping(RouteManager routeManager) throws BeansException {
        for (Object routeBean : getRouteBeans()) {

            Class<?> beanClass = AopUtils.getTargetClass(routeBean);
            String baseUrl = resolveBaseUrl(beanClass);

            for (Method method : beanClass.getDeclaredMethods()) {

                if (!AnnotationUtils.isAnnotationPresent(method, RouteMapping.class)) {
                    continue;
                }

                RouteMapping routeMapping = AnnotationUtils.getMergedAnnotation(method, RouteMapping.class);

                RouteAttribute attribute = RouteAttribute.builder()
                        .url(baseUrl + routeMapping.value())
                        .httpMethod(routeMapping.method())
                        .build();

                resolveAuthInfo(attribute, beanClass, method);

                routeManager.handle(attribute, new RouteManager.RouteInvocationHolder(routeBean, method));
            }
        }
    }

    private void resolveAuthInfo(RouteAttribute routeAttribute, Class<?> beanClass, Method routeMethod) {
        if (!checkIfAuth(beanClass, routeMethod)) {
            return;
        }
        routeAttribute.setAuth(true);

        boolean permitAll = AnnotationUtils.isAnnotationPresent(routeMethod, PermitAll.class);

        routeAttribute.setPermitAll(permitAll);

        if (permitAll) {
            return;
        }

        PermitAllowed permitAllowed = AnnotationUtils.getMergedAnnotation(routeMethod, PermitAllowed.class);
        if (permitAllowed != null) {
            routeAttribute.setPermitAllowed(new PermitHolder(permitAllowed));
        }

        RolesAllowed rolesAllowed = AnnotationUtils.getMergedAnnotation(routeMethod, RolesAllowed.class);
        if (rolesAllowed != null) {
            routeAttribute.setRolesAllowed(new PermitHolder(rolesAllowed));
        }

    }

    private boolean checkIfAuth(Class<?> beanClass, Method routeMethod) {
        return AnnotationUtils.isAnnotationPresent(beanClass, Auth.class) || AnnotationUtils.isAnnotationPresent(routeMethod, Auth.class);
    }

    private String resolveBaseUrl(Class<?> beanClass) {
        if (AnnotationUtils.isAnnotationPresent(beanClass, RouteMapping.class)) {
            return Objects.requireNonNull(AnnotationUtils.getMergedAnnotation(beanClass, RouteMapping.class))
                    .value();
        }
        return "";
    }

    private AuthHandler getAuthHandler() throws BeansException {
        AuthHandler authHandler = null;
        try {
            authHandler = beanFactory.getBean(AuthHandler.class);
        } catch (NoSuchBeanException e) {
            if (log.isDebugEnabled()) {
                log.debug("no auth handler found!");
            }
        }
        return authHandler;
    }

    private List<Object> getRouteBeans() throws BeansException {
        List<Object> beans = new LinkedList<>();
        for (BeanDefinition beanDefinition : beanFactory.getBeanDefinitions().values()) {
            if (AnnotationUtils.isAnnotationPresent(beanDefinition.getBeanClass(), RouteController.class)) {
                String beanName = beanDefinition.getBeanName();
                Object bean = beanFactory.getBean(beanName);
                beans.add(bean);
            }
        }
        return beans;
    }

    private List<Filter> getOrderedFilters(List<Filter> filters) throws BeansException {
        try {
            filters.addAll(beanFactory.getBeans(Filter.class).values());
        } catch (NoSuchBeanException e) {
            if (log.isDebugEnabled()) {
                log.debug("no filter found!");
            }
        }
        List<Filter> orderedfilters = BeanUtils.sortByOrder(filters);
        Collections.reverse(orderedfilters);
        return orderedfilters;
    }

    private void registerAuthFilterIfNeed(List<Filter> filters) throws BeansException {
        try {
            beanFactory.getBean(AuthFilter.class);
        } catch (NoSuchBeanException e) {
            filters.add(new AuthFilter());
        }
    }

    private void registerBodyHandler(io.vertx.reactivex.ext.web.Router router) {
        BodyHandler bodyHandler = BodyHandler.create();
        resolveAndSetBodyProperties(bodyHandler);
        router.route().handler(bodyHandler);
    }

    private void resolveAndSetBodyProperties(BodyHandler bodyHandler) {
        String uploadsDirectory = resourceReader.readProperty(ApplicationProperty.SERVER_UPLOAD_DERICTORY);
        if (!StringUtils.isEmpty(uploadsDirectory)) {
            bodyHandler.setUploadsDirectory(uploadsDirectory);
        }

        Object limit = resourceReader.readProperty(ApplicationProperty.SERVER_UPLOAD_LIMIT);
        if (limit instanceof Integer) {
            bodyHandler.setBodyLimit((Integer) limit);
        } else if (limit instanceof Long) {
            bodyHandler.setBodyLimit((Long) limit);
        }

    }

    private void startServer(io.vertx.reactivex.ext.web.Router router) {
        InetSocketAddress inetSocketAddress = resolveAddress();
        httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router)
                .rxListen(inetSocketAddress.getPort(), inetSocketAddress.getHostName())
                .subscribe(httpServer -> {
                    if (log.isInfoEnabled()) {
                        log.info("stated http server at {}:{}!",
                                inetSocketAddress.getHostName(), inetSocketAddress.getPort());
                    }
                }, e -> {
                    if (log.isErrorEnabled()) {
                        log.error("failed to start http server at {}:{}!",
                                inetSocketAddress.getHostName(), inetSocketAddress.getPort(), e);
                    }
                    vertx.close();
                });
    }

    private InetSocketAddress resolveAddress() {
        String host = resourceReader.readProperty(ApplicationProperty.SERVER_HOST, ApplicationProperty.DEFAULT_HOST);
        Integer port = resourceReader.readProperty(ApplicationProperty.SERVER_PORT, ApplicationProperty.DEFAULT_PORT);
        return new InetSocketAddress(host, port);
    }

    @Override
    public Completable rxStop() {
        return httpServer.rxClose().doOnComplete(() -> log.info("closed web server !"));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    public void setResourceReader(ResourceReader resourceReader) throws BeansException {
        this.resourceReader = resourceReader;
    }

    @Override
    public void setRouter(Router router) throws BeansException {
        this.router = router;
    }
}
