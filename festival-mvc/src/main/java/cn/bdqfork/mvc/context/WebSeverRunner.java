package cn.bdqfork.mvc.context;

import cn.bdqfork.context.aware.BeanFactoryAware;
import cn.bdqfork.context.aware.ResourceReaderAware;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.mvc.annotation.RouteMapping;
import cn.bdqfork.mvc.handler.GenericMappingHandler;
import cn.bdqfork.value.reader.ResourceReader;
import io.reactivex.Completable;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
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

        List<BeanDefinition> beanDefinitions = getRouteBeanDefinitions();

        for (BeanDefinition beanDefinition : beanDefinitions) {

            Class<?> beanClass = beanDefinition.getBeanClass();

            String baseUrl = beanClass.getAnnotation(RouteMapping.class).value();

            for (Method declaredMethod : beanClass.getDeclaredMethods()) {

                Object bean = getRouteBean(beanDefinition);

                new GenericMappingHandler(vertx).handle(router, bean, baseUrl, declaredMethod);
            }
        }

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

        httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router);

        String host = resourceReader.readProperty("server.host");
        Integer port = resourceReader.readProperty("server.port");

        if (StringUtils.isEmpty(host)) {
            host = DEFAULT_HOST;
        }

        if (port == null) {
            port = DEFAULT_PORT;
        }

        String finalHost = host;
        Integer finalPort = port;

        httpServer.rxListen(port, host)
                .subscribe(httpServer -> {
                    if (log.isInfoEnabled()) {
                        log.info("stated http server by host:{} and port:{}!", finalHost, finalPort);
                    }
                }, e -> {
                    if (log.isErrorEnabled()) {
                        log.error("failed to start http server by host:{} and port:{}!", finalHost, finalPort, e);
                    }
                    vertx.close();
                });
        return super.rxStart();
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
