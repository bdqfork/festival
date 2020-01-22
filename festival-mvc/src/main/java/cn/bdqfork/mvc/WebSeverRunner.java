package cn.bdqfork.mvc;

import cn.bdqfork.context.aware.BeanFactoryAware;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.mvc.annotation.Route;
import cn.bdqfork.mvc.handler.GenericMappingHandler;
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
public class WebSeverRunner extends AbstractVerticle implements BeanFactoryAware {
    private GenericMappingHandler mappingHandler = new GenericMappingHandler();
    private ConfigurableBeanFactory configurableBeanFactory;
    private HttpServer httpServer;

    @Override
    public Completable rxStart() {
        Router router = Router.router(vertx);

        List<BeanDefinition> beanDefinitions = getRouteBeanDefinitions();

        for (BeanDefinition beanDefinition : beanDefinitions) {

            Class<?> beanClass = beanDefinition.getBeanClass();

            String baseUrl = beanClass.getAnnotation(Route.class).value();

            for (Method declaredMethod : beanClass.getDeclaredMethods()) {

                Object bean = getRouteBean(beanDefinition);

                mappingHandler.handle(router, bean, baseUrl, declaredMethod);
            }
        }
        router.route().handler(BodyHandler.create());

        httpServer = vertx.createHttpServer();

        return httpServer
                .requestHandler(router)
                .rxListen(8080)
                .toCompletable();
    }

    private List<BeanDefinition> getRouteBeanDefinitions() {
        return configurableBeanFactory.getBeanDefinitions()
                .values()
                .stream()
                .filter(beanDefinition -> beanDefinition.getBeanClass().isAnnotationPresent(Route.class))
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

}
