package cn.bdqfork.mvc;

import cn.bdqfork.context.aware.BeanFactoryAware;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.mvc.annotation.Route;
import cn.bdqfork.mvc.handler.GenericMappingHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Slf4j
public class WebApplicationRunner extends AbstractVerticle implements BeanFactoryAware {
    private GenericMappingHandler mappingHandler = new GenericMappingHandler();
    private ConfigurableBeanFactory configurableBeanFactory;
    private HttpServer server;

    @Override
    public void start() throws Exception {
        server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        List<BeanDefinition> beanDefinitions = configurableBeanFactory.getBeanDefinitions()
                .values()
                .stream()
                .filter(beanDefinition -> beanDefinition.getBeanClass().isAnnotationPresent(Route.class))
                .collect(Collectors.toList());

        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> beanClass = beanDefinition.getBeanClass();
            String baseUrl = beanClass.getAnnotation(Route.class).value();
            for (Method declaredMethod : beanClass.getDeclaredMethods()) {
                Object bean = configurableBeanFactory.getBean(beanDefinition.getBeanName());
                mappingHandler.handle(router, bean, baseUrl, declaredMethod);
            }
        }

        server.requestHandler(router).listen(8080);
    }

    @Override
    public void stop() throws Exception {
        server.close(result -> {
            if (result.succeeded()) {
                log.info("closed web server !");
            }
        });
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
    }
}
