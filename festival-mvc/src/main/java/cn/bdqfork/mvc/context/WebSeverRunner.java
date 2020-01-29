package cn.bdqfork.mvc.context;

import cn.bdqfork.context.aware.BeanFactoryAware;
import cn.bdqfork.context.aware.ResourceReaderAware;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.mvc.constant.ApplicationProperty;
import cn.bdqfork.mvc.context.annotation.Router;
import cn.bdqfork.mvc.context.filter.AuthFilter;
import cn.bdqfork.mvc.context.filter.Filter;
import cn.bdqfork.mvc.context.handler.DefaultMappingHandler;
import cn.bdqfork.value.reader.ResourceReader;
import io.reactivex.Completable;
import io.vertx.core.Promise;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.auth.AuthProvider;
import io.vertx.reactivex.ext.web.handler.AuthHandler;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.SessionHandler;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;
import io.vertx.reactivex.ext.web.sstore.SessionStore;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Slf4j
public class WebSeverRunner extends AbstractVerticle implements BeanFactoryAware, ResourceReaderAware {
    private ConfigurableBeanFactory beanFactory;
    private HttpServer httpServer;
    private ResourceReader resourceReader;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        io.vertx.reactivex.ext.web.Router router = io.vertx.reactivex.ext.web.Router.router(vertx);

        registerSessionHandler(router);

        registerRouteMapping(router);

        registerBodyHandler(router);

        startServer(router);

        startPromise.complete();
    }

    private void registerSessionHandler(io.vertx.reactivex.ext.web.Router router) throws BeansException {
        SessionStore sessionStore = getOrCreateSessionStore();

        SessionHandler sessionHandler = SessionHandler.create(sessionStore);

        resolveAndSetSessionProperties(sessionHandler);

        AuthProvider authProvider = getAuthProvider();

        sessionHandler.setAuthProvider(authProvider);

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
            if (log.isTraceEnabled()) {
                log.trace("no auth provider");
            }
        }
        return authProvider;
    }

    private void registerRouteMapping(io.vertx.reactivex.ext.web.Router router) throws Exception {

        AuthHandler authHandler = getAuthHandler();

        RouteResolver routeResolver = new RouteResolver(router, authHandler);

        DefaultMappingHandler mappingHandler = new DefaultMappingHandler(vertx);

        List<RouteAttribute> routeAttributes = routeResolver.resolve(getRouteBeans());

        routeAttributes.forEach(mappingHandler::handle);

        getFilters().forEach(mappingHandler::registerFilter);

        registerAuthFilterIfNeed(mappingHandler);


    }

    private AuthHandler getAuthHandler() throws BeansException {
        AuthHandler authHandler = null;
        try {
            authHandler = beanFactory.getBean(AuthHandler.class);
        } catch (NoSuchBeanException e) {
            if (log.isTraceEnabled()) {
                log.trace("no auth handler found!");
            }
        }
        return authHandler;
    }

    private List<Object> getRouteBeans() throws BeansException {
        List<Object> beans = new LinkedList<>();
        for (BeanDefinition beanDefinition : beanFactory.getBeanDefinitions().values()) {
            if (AnnotationUtils.isAnnotationPresent(beanDefinition.getBeanClass(), Router.class)) {
                String beanName = beanDefinition.getBeanName();
                Object bean = beanFactory.getBean(beanName);
                beans.add(bean);
            }
        }
        return beans;
    }

    private Collection<Filter> getFilters() throws BeansException {
        try {
            return beanFactory.getBeans(Filter.class).values();
        } catch (NoSuchBeanException e) {
            if (log.isTraceEnabled()) {
                log.trace("no filter found!");
            }
            return Collections.emptyList();
        }
    }

    private void registerAuthFilterIfNeed(DefaultMappingHandler mappingHandler) throws BeansException {
        try {
            beanFactory.getBean(AuthFilter.class);
        } catch (NoSuchBeanException e) {
            mappingHandler.registerFilter(new AuthFilter());
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

        Long limit = resourceReader.readProperty(ApplicationProperty.SERVER_UPLOAD_LIMIT);
        if (limit != null) {
            bodyHandler.setBodyLimit(limit);
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

}
