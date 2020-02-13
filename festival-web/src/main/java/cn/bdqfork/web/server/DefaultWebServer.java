package cn.bdqfork.web.server;

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
import cn.bdqfork.web.constant.ServerProperty;
import cn.bdqfork.web.route.*;
import cn.bdqfork.web.route.annotation.RouteController;
import cn.bdqfork.web.route.filter.Filter;
import cn.bdqfork.web.route.filter.FilterChainFactory;
import io.reactivex.disposables.Disposable;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.net.JksOptions;
import io.vertx.reactivex.core.net.SocketAddress;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author bdq
 * @since 2020/2/12
 */
@Slf4j
public class DefaultWebServer extends AbstractWebServer implements BeanFactoryAware, ResourceReaderAware {
    private ConfigurableBeanFactory beanFactory;
    private ResourceReader resourceReader;
    private Disposable disposable;

    @Override
    protected void registerCoreHandler(Router router) throws Exception {
        router.route().handler(ResponseContentTypeHandler.create());

        registerSessionHandler(router);

        registerBodyHandler(router);
    }

    private void registerSessionHandler(Router router) throws Exception {
        SessionManager sessionManager = new SessionManager(router, vertx);
        sessionManager.setBeanFactory(beanFactory);
        sessionManager.setResourceReader(resourceReader);
        sessionManager.registerSessionHandler();
    }

    private void registerBodyHandler(Router router) {
        BodyHandler bodyHandler = BodyHandler.create();

        String uploadsDirectory = resourceReader.readProperty(ServerProperty.SERVER_UPLOAD_DERICTORY, String.class);
        if (!StringUtils.isEmpty(uploadsDirectory)) {
            bodyHandler.setUploadsDirectory(uploadsDirectory);
        }

        Long limit = resourceReader.readProperty(ServerProperty.SERVER_UPLOAD_LIMIT, Long.class);
        if (limit != null) {
            bodyHandler.setBodyLimit(limit);
        }

        router.route().handler(bodyHandler);
    }

    @Override
    protected void registerOptionHandler(Router router) throws Exception {
        try {
            LoggerHandler loggerHandler = beanFactory.getBean(LoggerHandler.class);
            router.route().handler(loggerHandler);
        } catch (NoSuchBeanException e) {
            if (log.isDebugEnabled()) {
                log.debug("no logger handler registed!");
            }
        }
        try {
            ErrorHandler errorHandler = beanFactory.getBean(ErrorHandler.class);
            router.route().handler(errorHandler);
        } catch (NoSuchBeanException e) {
            if (log.isDebugEnabled()) {
                log.debug("no error handler registed!");
            }
        }

        try {
            CorsHandler corsHandler = beanFactory.getBean(CorsHandler.class);
            router.route().handler(corsHandler);
        } catch (NoSuchBeanException e) {
            if (log.isDebugEnabled()) {
                log.debug("no cors handler registed!");
            }
        }
    }

    @Override
    protected void registerRouteMapping(Router router) throws Exception {
        FilterChainFactory filterChainFactory = new FilterChainFactory();
        filterChainFactory.registerFilters(getOrderedFilters());

        AuthHandler authHandler = null;
        try {
            authHandler = beanFactory.getBean(AuthHandler.class);
        } catch (NoSuchBeanException e) {
            if (log.isDebugEnabled()) {
                log.debug("no auth handler found!");
            }
        }

        RouteManager routeManager = new RouteManager(router, filterChainFactory, authHandler);

        RouteResolver routeResolver = new RouteResolver();
        Map<RouteAttribute, RouteInvocation> routes = routeResolver.resovle(getRouteBeans());
        routes.forEach(routeManager::handle);

        Collection<RouteAttribute> customRoutes = beanFactory.getBeans(RouteAttribute.class).values();
        customRoutes.forEach(routeManager::handle);
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

    @Override
    protected void doStart() throws Exception {
        HttpServerOptions options = resolveHttpServerOptions();

        disposable = vertx.createHttpServer(options)
                .requestHandler(router)
                .rxListen()
                .doOnDispose(() -> {
                    log.info("closed web server !");
                })
                .subscribe(httpServer -> {
                    if (log.isInfoEnabled()) {
                        log.info("stated web server at {}:{}!",
                                options.getHost(), options.getPort());
                    }
                }, e -> {
                    if (log.isErrorEnabled()) {
                        log.error("failed to start web server at {}:{}!",
                                options.getHost(), options.getPort(), e);
                    }
                    vertx.close();
                });
    }

    private HttpServerOptions resolveHttpServerOptions() throws BeansException {
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

        SocketAddress socketAddress = resolveAddress();
        options.setHost(socketAddress.host())
                .setPort(socketAddress.port());

        Boolean enableHttp2 = resourceReader.readProperty(ServerProperty.SERVER_HTTP2_ENABLE, Boolean.class, false);
        if (enableHttp2) {
            if (log.isInfoEnabled()) {
                log.info("http2 enabled!");
            }
            options.getAlpnVersions().add(HttpVersion.HTTP_2);
        }

        Boolean sslEnable = resourceReader.readProperty(ServerProperty.SERVER_SSL_ENABLE, Boolean.class, false);
        if (sslEnable) {
            options.setSsl(sslEnable);
            String path = resourceReader.readProperty(ServerProperty.SERVER_SSL_PATH, String.class);
            String password = resourceReader.readProperty(ServerProperty.SERVER_SSL_PASSWORD, String.class);
            JksOptions jksOptions = new JksOptions()
                    .setPath(path)
                    .setPassword(password);
            options.setKeyCertOptions(jksOptions);
        }

        return options;
    }

    private SocketAddress resolveAddress() {
        String host = resourceReader.readProperty(ServerProperty.SERVER_HOST, String.class, ServerProperty.DEFAULT_HOST);
        Integer port = resourceReader.readProperty(ServerProperty.SERVER_PORT, Integer.class, ServerProperty.DEFAULT_PORT);
        return SocketAddress.inetSocketAddress(port, host);
    }

    @Override
    protected void doStop() throws Exception {
        disposable.dispose();
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
