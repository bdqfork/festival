package cn.bdqfork.mvc.context;

import cn.bdqfork.context.aware.BeanFactoryAware;
import cn.bdqfork.context.aware.ResourceReaderAware;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.mvc.mapping.MappingAttribute;
import cn.bdqfork.mvc.mapping.annotation.RouteMapping;
import cn.bdqfork.mvc.mapping.filter.AuthFilter;
import cn.bdqfork.mvc.mapping.handler.DefaultMappingHandler;
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
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Slf4j
public class WebSeverRunner extends AbstractVerticle implements BeanFactoryAware, ResourceReaderAware {
    private static final String DEFAULT_HOST = "localhost";
    private static final Integer DEFAULT_PORT = 8080;
    private ConfigurableBeanFactory configurableBeanFactory;
    private HttpServer httpServer;
    private ResourceReader resourceReader;

    @Override
    public Completable rxStart() {

        Router router = Router.router(vertx);

        SecuritySystemManager securitySystemManager = getSecurityManager();

        if (securitySystemManager != null) {

            AuthProvider authProvider = securitySystemManager.getAuthProvider();

            AuthHandler authHandler = securitySystemManager.getAuthHandler();

            if (authProvider != null && authHandler != null) {
                router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)).setAuthProvider(authProvider));
            }

            processMapping(router, securitySystemManager);
        } else {
            processMapping(router, null);
        }


        processBody(router);

        String host = getHost();
        Integer port = getPort();

        start(router, host, port);

        return super.rxStart();
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

    private void processMapping(Router router, SecuritySystemManager securitySystemManager) {
        List<BeanDefinition> beanDefinitions = getRouteBeanDefinitions();

        for (BeanDefinition beanDefinition : beanDefinitions) {

            Class<?> beanClass = beanDefinition.getBeanClass();

            String baseUrl = "";
            if (AnnotationUtils.isAnnotationPresent(beanClass, RouteMapping.class)) {
                baseUrl = beanClass.getAnnotation(RouteMapping.class).value();
            }

            for (Method declaredMethod : beanClass.getDeclaredMethods()) {

                if (!AnnotationUtils.isAnnotationPresent(declaredMethod, RouteMapping.class)) {
                    continue;
                }

                Object bean = getRouteBean(beanDefinition);

                MappingAttribute.Builder builder = MappingAttribute.builder().setRouter(router)
                        .setRouteMethod(declaredMethod)
                        .setBean(bean)
                        .setBaseUrl(baseUrl);

                if (securitySystemManager != null) {
                    builder.setAuthHandler(securitySystemManager.getAuthHandler());
                }

                MappingAttribute attribute = builder.build();

                DefaultMappingHandler mappingHandler = new DefaultMappingHandler(vertx);

                mappingHandler.registerFilter(new AuthFilter(securitySystemManager, attribute));

                mappingHandler.handle(attribute);
            }
        }
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

    private String getHost() {
        String host = resourceReader.readProperty("server.host");
        if (StringUtils.isEmpty(host)) {
            host = DEFAULT_HOST;
        }
        return host;
    }

    private Integer getPort() {
        Integer port = resourceReader.readProperty("server.port");
        if (port == null) {
            port = DEFAULT_PORT;
        }
        return port;
    }

    private void processBody(Router router) {
        BodyHandler bodyHandler = BodyHandler.create();

        String uploadsDirectory = resourceReader.readProperty("server.uploads.directory");
        if (!StringUtils.isEmpty(uploadsDirectory)) {
            bodyHandler.setUploadsDirectory(uploadsDirectory);
        }

        Long limit = resourceReader.readProperty("server.uploads.limit");
        if (limit != null) {
            bodyHandler.setBodyLimit(limit);
        }

        router.route().handler(bodyHandler);
    }

    private void start(Router router, String host, Integer port) {
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
