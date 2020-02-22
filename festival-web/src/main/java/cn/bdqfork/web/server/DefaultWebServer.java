package cn.bdqfork.web.server;

import cn.bdqfork.context.aware.BeanFactoryAware;
import cn.bdqfork.context.aware.ResourceReaderAware;
import cn.bdqfork.context.configuration.reader.ResourceReader;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.web.constant.ServerProperty;
import cn.bdqfork.web.route.RouteManager;
import cn.bdqfork.web.route.SessionManager;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2020/2/12
 */
public class DefaultWebServer extends AbstractWebServer implements BeanFactoryAware, ResourceReaderAware {
    private static final Logger log = LoggerFactory.getLogger(DefaultWebServer.class);
    private ConfigurableBeanFactory beanFactory;
    private ResourceReader resourceReader;
    private HttpServer httpServer;

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

        boolean staticEnable = resourceReader.readProperty(ServerProperty.SERVER_STATIC_ENABLE, Boolean.class, false);

        if (staticEnable) {

            configStaticHandler(router);
        }
    }

    private void configStaticHandler(Router router) {
        String webRoot = resourceReader.readProperty(ServerProperty.SERVER_STATIC_ROOT, String.class,
                ServerProperty.DEFAULT_STATIC_ROOT);
        StaticHandler staticHandler = StaticHandler.create(webRoot);

        boolean cacheEnable = resourceReader.readProperty(ServerProperty.SERVER_STATIC_CACHE_ENABLE, Boolean.class,
                StaticHandler.DEFAULT_CACHING_ENABLED);
        staticHandler.setCachingEnabled(cacheEnable);

        Integer size = resourceReader.readProperty(ServerProperty.SERVER_STATIC_CACHE_SIZE, Integer.class,
                StaticHandler.DEFAULT_MAX_CACHE_SIZE);
        staticHandler.setMaxCacheSize(size);

        Long age = resourceReader.readProperty(ServerProperty.SERVER_STATIC_CACHE_AGE, Long.class,
                StaticHandler.DEFAULT_CACHE_ENTRY_TIMEOUT);
        staticHandler.setCacheEntryTimeout(age);

        String staticPath = resourceReader.readProperty(ServerProperty.SERVER_STATIC_PATH, String.class,
                ServerProperty.DEFAULT_STATIC_PATH);
        router.route(staticPath).handler(staticHandler);
    }

    @Override
    protected void registerRouteMapping(Router router) throws Exception {
        RouteManager routeManager = new RouteManager(beanFactory, vertx, router);
        routeManager.registerRouteMapping();
    }


    @Override
    protected void doStart() throws Exception {
        WebSocketRouter webSocketRouter = new WebSocketRouter(beanFactory);

        HttpServerOptions options = resolveHttpServerOptions();
        httpServer = vertx.createHttpServer(options)
                .websocketHandler(webSocketRouter::accept)
                .requestHandler(router)
                .listen(res -> {
                    if (res.succeeded()) {
                        if (log.isInfoEnabled()) {
                            log.info("started web server at {}:{}!",
                                    options.getHost(), options.getPort());
                        }
                    } else {
                        if (log.isErrorEnabled()) {
                            log.error("failed to start web server at {}:{}!",
                                    options.getHost(), options.getPort(), res.cause());
                        }
                        vertx.close();
                    }
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
        httpServer.close(res -> {
            if (res.succeeded()) {
                if (log.isInfoEnabled()) {
                    log.info("closed server!");
                }
            } else {
                if (log.isErrorEnabled()) {
                    log.error("failed to close server!", res.cause());
                }
            }
        });
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
