package cn.bdqfork.mvc.context;

import cn.bdqfork.context.aware.BeanFactoryAware;
import cn.bdqfork.context.aware.ResourceReaderAware;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.mvc.constant.ApplicationProperty;
import cn.bdqfork.mvc.context.annotation.RouteMapping;
import cn.bdqfork.mvc.context.filter.AuthFilter;
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
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Slf4j
public class WebSeverRunner extends AbstractVerticle implements BeanFactoryAware, ResourceReaderAware {
    private ConfigurableBeanFactory configurableBeanFactory;
    private HttpServer httpServer;
    private ResourceReader resourceReader;

    @Override
    public Completable rxStart() {

        Router router = Router.router(vertx);

        SecuritySystemManager securitySystemManager = getSecurityManager();

        if (securitySystemManager != null) {

            if (log.isInfoEnabled()) {
                log.info("process session config!");
            }

            SessionHandler sessionHandler = securitySystemManager.getSessionHandler();
            if (sessionHandler == null) {
                sessionHandler = createSessionHandler(securitySystemManager.getAuthProvider());
            }

            router.route().handler(sessionHandler);

        }

        AuthHandler authHandler = null;
        if (securitySystemManager != null) {
            authHandler = securitySystemManager.getAuthHandler();
        }

        List<BeanDefinition> beanDefinitions = getRouteBeanDefinitions();

        for (BeanDefinition beanDefinition : beanDefinitions) {

            Class<?> beanClass = beanDefinition.getBeanClass();

            String baseUrl = "";

            if (AnnotationUtils.isAnnotationPresent(beanClass, RouteMapping.class)) {
                baseUrl = Objects.requireNonNull(AnnotationUtils.getMergedAnnotation(beanClass, RouteMapping.class))
                        .value();
            }

            for (Method declaredMethod : beanClass.getDeclaredMethods()) {

                if (!AnnotationUtils.isAnnotationPresent(declaredMethod, RouteMapping.class)) {
                    continue;
                }

                Object bean = getRouteBean(beanDefinition);

                MappingAttribute attribute = MappingAttribute.builder().setRouter(router)
                        .setRouteMethod(declaredMethod)
                        .setBean(bean)
                        .setBaseUrl(baseUrl)
                        .setAuthHandler(authHandler)
                        .build();


                DefaultMappingHandler mappingHandler = new DefaultMappingHandler(vertx);

                mappingHandler.registerFilter(new AuthFilter(securitySystemManager, attribute));

                mappingHandler.handle(attribute);
            }
        }

        BodyHandler bodyHandler = BodyHandler.create();

        String uploadsDirectory = resourceReader.readProperty(ApplicationProperty.SERVER_UPLOAD_DERICTORY);
        if (!StringUtils.isEmpty(uploadsDirectory)) {
            bodyHandler.setUploadsDirectory(uploadsDirectory);
        }

        Long limit = resourceReader.readProperty(ApplicationProperty.SERVER_UPLOAD_LIMIT);
        if (limit != null) {
            bodyHandler.setBodyLimit(limit);
        }

        router.route().handler(bodyHandler);

        start(router);

        return super.rxStart();
    }

    private SessionHandler createSessionHandler(AuthProvider authProvider) {

        SessionHandler sessionHandler = SessionHandler.create(LocalSessionStore.create(vertx))
                .setAuthProvider(authProvider);

        boolean httpOnly = resourceReader.readProperty(ApplicationProperty.SERVER_COOKIE_HTTP_ONLY, true);
        sessionHandler.setCookieHttpOnlyFlag(httpOnly);

        boolean secure = resourceReader.readProperty(ApplicationProperty.SERVER_COOKIE_SECURE, false);
        sessionHandler.setCookieSecureFlag(secure);

        long timeout = resourceReader.readProperty(ApplicationProperty.SERVER_SESSION_TIMEOUT, 30 * 60 * 1000);
        sessionHandler.setSessionTimeout(timeout);

        String cookieName = resourceReader.readProperty(ApplicationProperty.SERVER_SESSION_COOKIE_NAME);
        if (!StringUtils.isEmpty(cookieName)) {
            sessionHandler.setSessionCookieName(cookieName);
        }

        String cookiePath = resourceReader.readProperty(ApplicationProperty.SERVER_SESSION_COOKIE_PATH);
        if (!StringUtils.isEmpty(cookiePath)) {
            sessionHandler.setSessionCookiePath(cookiePath);
        }
        return sessionHandler;
    }

    private SecuritySystemManager getSecurityManager() {
        try {
            return configurableBeanFactory.getBean(SecuritySystemManager.class);
        } catch (BeansException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage(), e);
            }
        }
        return null;
    }

    private List<BeanDefinition> getRouteBeanDefinitions() {
        return configurableBeanFactory.getBeanDefinitions()
                .values()
                .stream()
                .filter(beanDefinition -> beanDefinition.getBeanClass().isAnnotationPresent(RouteMapping.class))
                .collect(Collectors.toList());
    }

    private Object getRouteBean(BeanDefinition beanDefinition) {
        try {
            return configurableBeanFactory.getBean(beanDefinition.getBeanName());
        } catch (BeansException e) {
            throw new IllegalStateException(e);
        }
    }

    private void start(Router router) {
        String host = resourceReader.readProperty(ApplicationProperty.SERVER_HOST, ApplicationProperty.DEFAULT_HOST);
        Integer port = resourceReader.readProperty(ApplicationProperty.SERVER_PORT, ApplicationProperty.DEFAULT_PORT);

        httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router)
                .rxListen(port, host)
                .subscribe(httpServer -> {
                    if (log.isInfoEnabled()) {
                        log.info("stated http server by host:{} and port:{}!", host, port);
                    }
                }, e -> {
                    if (log.isErrorEnabled()) {
                        log.error("failed to start http server by host:{} and port:{}!", host, port, e);
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
        configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    public void setResourceReader(ResourceReader resourceReader) throws BeansException {
        this.resourceReader = resourceReader;
    }
}
