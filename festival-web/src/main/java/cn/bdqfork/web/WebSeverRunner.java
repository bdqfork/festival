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
import cn.bdqfork.core.util.BeanUtils;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.web.route.annotation.RouteController;
import cn.bdqfork.web.constant.ServerProperty;
import cn.bdqfork.web.route.RouteAttribute;
import cn.bdqfork.web.route.RouteManager;
import cn.bdqfork.web.route.RouteResolver;
import cn.bdqfork.web.route.SessionManager;
import cn.bdqfork.web.route.filter.Filter;
import cn.bdqfork.web.route.filter.FilterManager;
import io.reactivex.disposables.Disposable;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.net.SocketAddress;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.AuthHandler;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.LoggerHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Slf4j
public class WebSeverRunner extends AbstractVerticle implements BeanFactoryAware, ResourceReaderAware, RouterAware {
    private ConfigurableBeanFactory beanFactory;
    private Disposable disposable;
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

    private void registerSessionHandler(Router router) throws BeansException {
        SessionManager sessionManager = new SessionManager(router, vertx);
        sessionManager.setBeanFactory(beanFactory);
        sessionManager.setResourceReader(resourceReader);
        sessionManager.registerSessionHandler();
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

    private void registerRouteMapping(Router router) throws Exception {
        FilterManager filterManager = new FilterManager();

        filterManager.registerFilters(getOrderedFilters());

        AuthHandler authHandler = getAuthHandler();

        RouteManager routeManager = new RouteManager(router, filterManager, authHandler);

        RouteResolver routeResolver = new RouteResolver();

        routeResolver.resovle(getRouteBeans()).forEach(routeManager::handle);

        Collection<RouteAttribute> customRoutes = getCustomRoutes();

        customRoutes.forEach(routeManager::handle);

    }

    private Collection<RouteAttribute> getCustomRoutes() throws BeansException {
        return beanFactory.getBeans(RouteAttribute.class).values();
    }


    private AuthHandler getAuthHandler() throws BeansException {
        try {
            return beanFactory.getBean(AuthHandler.class);
        } catch (NoSuchBeanException e) {
            if (log.isDebugEnabled()) {
                log.debug("no auth handler found!");
            }
        }
        return null;
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

    private List<Filter> getOrderedFilters() throws BeansException {
        try {
            Collection<Filter> filters = beanFactory.getBeans(Filter.class).values();
            List<Filter> orderedfilters = BeanUtils.sortByOrder(filters);
            Collections.reverse(orderedfilters);
            return orderedfilters;
        } catch (NoSuchBeanException e) {
            if (log.isDebugEnabled()) {
                log.debug("no filter found!");
            }
            return Collections.emptyList();
        }
    }

    private void registerBodyHandler(io.vertx.reactivex.ext.web.Router router) {
        BodyHandler bodyHandler = BodyHandler.create();
        resolveAndSetBodyProperties(bodyHandler);
        router.route().handler(bodyHandler);
    }

    private void resolveAndSetBodyProperties(BodyHandler bodyHandler) {
        String uploadsDirectory = resourceReader.readProperty(ServerProperty.SERVER_UPLOAD_DERICTORY);
        if (!StringUtils.isEmpty(uploadsDirectory)) {
            bodyHandler.setUploadsDirectory(uploadsDirectory);
        }

        Object limit = resourceReader.readProperty(ServerProperty.SERVER_UPLOAD_LIMIT);
        if (limit instanceof Integer) {
            bodyHandler.setBodyLimit((Integer) limit);
        } else if (limit instanceof Long) {
            bodyHandler.setBodyLimit((Long) limit);
        }

    }

    private void startServer(io.vertx.reactivex.ext.web.Router router) throws BeansException {
        SocketAddress socketAddress = resolveAddress();
        HttpServerOptions options = resolveHttpServerOptions(socketAddress);

        disposable = vertx.createHttpServer(options)
                .requestHandler(router)
                .rxListen()
                .doOnDispose(() -> {
                    log.info("closed web server !");
                })
                .subscribe(httpServer -> {
                    if (log.isInfoEnabled()) {
                        log.info("stated web server at {}:{}!",
                                socketAddress.host(), socketAddress.port());
                    }
                }, e -> {
                    if (log.isErrorEnabled()) {
                        log.error("failed to start web server at {}:{}!",
                                socketAddress.host(), socketAddress.port(), e);
                    }
                    vertx.close();
                });
    }

    private HttpServerOptions resolveHttpServerOptions(SocketAddress socketAddress) throws BeansException {
        HttpServerOptions options;
        try {
            options = beanFactory.getBean(HttpServerOptions.class);
            options = new HttpServerOptions(options);
        } catch (NoSuchBeanException e) {
            if (log.isInfoEnabled()) {
                log.info("use default web server options!");
            }
            options = new HttpServerOptions();
        }

        options.setHost(socketAddress.host())
                .setPort(socketAddress.port());

        Boolean sslEnable = resourceReader.readProperty(ServerProperty.SERVER_SSL_ENABLE, false);
        if (sslEnable) {
            options.setSsl(sslEnable);
            String path = resourceReader.readProperty(ServerProperty.SERVER_SSL_PATH);
            String password = resourceReader.readProperty(ServerProperty.SERVER_SSL_PASSWORD);
            JksOptions jksOptions = new JksOptions()
                    .setPath(path)
                    .setPassword(password);
            options.setKeyCertOptions(jksOptions);
        }
        return options;
    }

    private SocketAddress resolveAddress() {
        String host = resourceReader.readProperty(ServerProperty.SERVER_HOST, ServerProperty.DEFAULT_HOST);
        Integer port = resourceReader.readProperty(ServerProperty.SERVER_PORT, ServerProperty.DEFAULT_PORT);
        return SocketAddress.inetSocketAddress(port, host);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        disposable.dispose();
        stopPromise.complete();
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
