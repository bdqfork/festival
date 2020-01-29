package cn.bdqfork.mvc.context;

import cn.bdqfork.context.aware.BeanFactoryAware;
import cn.bdqfork.context.aware.ResourceReaderAware;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.AopUtils;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.mvc.constant.ApplicationProperty;
import cn.bdqfork.mvc.context.annotation.Route;
import cn.bdqfork.mvc.context.annotation.RouteMapping;
import cn.bdqfork.mvc.context.filter.AuthFilter;
import cn.bdqfork.mvc.context.filter.Filter;
import cn.bdqfork.mvc.context.handler.DefaultMappingHandler;
import cn.bdqfork.value.reader.ResourceReader;
import io.reactivex.Completable;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.auth.AuthProvider;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.AuthHandler;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.SessionHandler;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;
import io.vertx.reactivex.ext.web.sstore.SessionStore;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Completable rxStart() {

        Router router = Router.router(vertx);

        SessionStore sessionStore;
        try {
            sessionStore = beanFactory.getBean(SessionStore.class);
        } catch (NoSuchBeanException e) {
            sessionStore = LocalSessionStore.create(vertx);
        } catch (BeansException e) {
            throw new IllegalStateException(e);
        }

        SessionHandler sessionHandler = SessionHandler.create(sessionStore);

        setSessionConfig(sessionHandler);

        router.route().handler(sessionHandler);

        try {
            handleRouteMapping(router);
        } catch (BeansException e) {
            throw new IllegalStateException(e);
        }

        BodyHandler bodyHandler = BodyHandler.create();
        setBodyHandlerConfig(bodyHandler);
        router.route().handler(bodyHandler);

        startServer(router);

        return super.rxStart();
    }

    private void setBodyHandlerConfig(BodyHandler bodyHandler) {
        String uploadsDirectory = resourceReader.readProperty(ApplicationProperty.SERVER_UPLOAD_DERICTORY);
        if (!StringUtils.isEmpty(uploadsDirectory)) {
            bodyHandler.setUploadsDirectory(uploadsDirectory);
        }

        Long limit = resourceReader.readProperty(ApplicationProperty.SERVER_UPLOAD_LIMIT);
        if (limit != null) {
            bodyHandler.setBodyLimit(limit);
        }
    }

    public void handleRouteMapping(Router router) throws BeansException {
        AuthHandler authHandler = null;
        try {
            authHandler = beanFactory.getBean(AuthHandler.class);
        } catch (NoSuchBeanException e) {
            if (log.isTraceEnabled()) {
                log.trace("no auth handler found!");
            }
        }

        DefaultMappingHandler mappingHandler = new DefaultMappingHandler(vertx);

        for (Object routeBean : getRouteBeans()) {

            Class<?> beanClass = AopUtils.getTargetClass(routeBean);

            String baseUrl = "";

            if (AnnotationUtils.isAnnotationPresent(beanClass, RouteMapping.class)) {
                baseUrl = Objects.requireNonNull(AnnotationUtils.getMergedAnnotation(beanClass, RouteMapping.class))
                        .value();
            }

            for (Method declaredMethod : beanClass.getDeclaredMethods()) {

                if (!AnnotationUtils.isAnnotationPresent(declaredMethod, RouteMapping.class)) {
                    continue;
                }

                RouteAttribute attribute = RouteAttribute.builder()
                        .setRouter(router)
                        .setRouteMethod(declaredMethod)
                        .setBean(routeBean)
                        .setBaseUrl(baseUrl)
                        .setAuthHandler(authHandler)
                        .build();

                mappingHandler.handle(attribute);

            }

            Collection<Filter> filters = new LinkedList<>();

            try {
                filters.addAll(beanFactory.getBeans(Filter.class).values());
            } catch (NoSuchBeanException e) {
                if (log.isTraceEnabled()) {
                    log.trace("no filter found!");
                }
            }

            for (Filter filter : filters) {
                mappingHandler.registerFilter(filter);
            }

            try {
                beanFactory.getBean(AuthFilter.class);
            } catch (NoSuchBeanException e) {
                mappingHandler.registerFilter(new AuthFilter());
            }

        }


    }

    private Set<Object> getRouteBeans() {
        return beanFactory.getBeanDefinitions().values()
                .stream()
                .filter(beanDefinition -> {
                    return AnnotationUtils.isAnnotationPresent(beanDefinition.getBeanClass(), Route.class);
                })
                .map(BeanDefinition::getBeanName)
                .map(beanName -> {
                    try {
                        return beanFactory.getBean(beanName);
                    } catch (BeansException e) {
                        throw new IllegalStateException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    private void setSessionConfig(SessionHandler sessionHandler) {
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

        AuthProvider authProvider = null;
        try {
            authProvider = beanFactory.getBean(AuthProvider.class);
        } catch (BeansException e) {
            if (log.isTraceEnabled()) {
                log.trace("no auth provider");
            }
        }

        if (authProvider != null) {
            sessionHandler.setAuthProvider(authProvider);
        }

    }

    private void startServer(Router router) {
        String host = resourceReader.readProperty(ApplicationProperty.SERVER_HOST, ApplicationProperty.DEFAULT_HOST);
        Integer port = resourceReader.readProperty(ApplicationProperty.SERVER_PORT, ApplicationProperty.DEFAULT_PORT);
        httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router)
                .rxListen(port, host)
                .subscribe(httpServer -> {
                    if (log.isInfoEnabled()) {
                        log.info("stated http server by host:{} and port:{}!",
                                host, port);
                    }
                }, e -> {
                    if (log.isErrorEnabled()) {
                        log.error("failed to start http server by host:{} and port:{}!",
                                host, port, e);
                    }
                    vertx.close();
                });
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
