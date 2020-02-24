package cn.bdqfork.web;

import cn.bdqfork.context.AnnotationApplicationContext;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;
import cn.bdqfork.web.route.response.ResponseHandlerFactory;
import cn.bdqfork.web.server.DefaultWebServer;
import cn.bdqfork.web.util.VertxUtils;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2020/1/21
 */
public class WebApplicationContext extends AnnotationApplicationContext {
    private static final Logger log = LoggerFactory.getLogger(WebApplicationContext.class);
    private Vertx vertx;
    private Router router;

    public WebApplicationContext(String... scanPaths) throws BeansException {
        super(scanPaths);
    }

    @Override
    protected void registerBean() throws BeansException {
        super.registerBean();
        registerVertx();
        registerRouter();
        registerWebServer();
        registerResponseHandlerFactory();
    }

    private void registerVertx() throws BeansException {
        try {
            vertx = getBeanFactory().getBean(Vertx.class);
        } catch (NoSuchBeanException e) {
            if (log.isDebugEnabled()) {
                log.debug("can't find Vertx, will use default!");
            }
            getBeanFactory().registerSingleton("vertx", VertxUtils.getVertx());
            vertx = getBeanFactory().getBean(Vertx.class);
        }
    }

    private void registerRouter() throws BeansException {
        try {
            router = getBeanFactory().getBean(Router.class);
        } catch (NoSuchBeanException e) {
            if (log.isDebugEnabled()) {
                log.debug("can't find Vertx, will use default!");
            }
            getBeanFactory().registerSingleton("router", Router.router(vertx));
            router = getBeanFactory().getBean(Router.class);
        }
    }

    private void registerWebServer() throws BeansException {
        BeanDefinitionRegistry registry = getBeanFactory();
        BeanDefinition beanDefinition = BeanDefinition.builder()
                .scope(BeanDefinition.SINGLETON)
                .beanClass(DefaultWebServer.class)
                .beanName("webserver")
                .build();
        registry.registerBeanDefinition(beanDefinition.getBeanName(), beanDefinition);
    }

    private void registerResponseHandlerFactory() throws BeansException {
        BeanDefinitionRegistry registry = getBeanFactory();
        BeanDefinition beanDefinition = BeanDefinition.builder()
                .scope(BeanDefinition.SINGLETON)
                .beanClass(ResponseHandlerFactory.class)
                .beanName("responseHandlerFactory")
                .build();
        registry.registerBeanDefinition(beanDefinition.getBeanName(), beanDefinition);
    }

    @Override
    protected void processEnvironment() throws BeansException {
        super.processEnvironment();
        try {
            for (VertxAware vertxAware : getBeanFactory().getBeans(VertxAware.class).values()) {
                vertxAware.setVertx(vertx);
            }
        } catch (NoSuchBeanException e) {
            if (log.isDebugEnabled()) {
                log.debug("no vertx aware found!");
            }
        }

        try {
            for (RouterAware routerAware : getBeanFactory().getBeans(RouterAware.class).values()) {
                routerAware.setRouter(router);
            }
        } catch (NoSuchBeanException e) {
            if (log.isDebugEnabled()) {
                log.debug("no router aware found!");
            }
        }

    }

}
