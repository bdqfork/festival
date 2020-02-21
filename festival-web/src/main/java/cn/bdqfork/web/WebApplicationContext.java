package cn.bdqfork.web;

import cn.bdqfork.cache.constant.CacheProperty;
import cn.bdqfork.cache.processor.CacheSupportProcessor;
import cn.bdqfork.context.AnnotationApplicationContext;
import cn.bdqfork.context.configuration.reader.ResourceReader;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;
import cn.bdqfork.web.processor.VerticleProxyProcessor;
import cn.bdqfork.web.server.DefaultWebServer;
import cn.bdqfork.web.server.WebServer;
import cn.bdqfork.web.server.WebVerticle;
import cn.bdqfork.web.service.JsonMessageCodec;
import cn.bdqfork.web.util.VertxUtils;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @author bdq
 * @since 2020/1/21
 */
public class WebApplicationContext extends AnnotationApplicationContext {
    public static final String SERVER_OPTIONS_NAME = "serverOptions";
    private static final Logger log = LoggerFactory.getLogger(WebApplicationContext.class);
    private Vertx vertx;
    private Router router;

    public WebApplicationContext(String... scanPaths) throws BeansException {
        super(scanPaths);
    }

    @Override
    public void start() throws Exception {
        super.start();

        BeanFactory beanFactory = getBeanFactory();

        vertx.eventBus().registerCodec(new JsonMessageCodec());

        DeploymentOptions options = getDeploymentOptions(beanFactory);

        WebServer webServer = beanFactory.getBean(WebServer.class);

        WebVerticle webVerticle = new WebVerticle(webServer);
        vertx.deployVerticle(webVerticle, options, res -> {
            if (res.failed()) {
                if (log.isErrorEnabled()) {
                    log.error("failed to deploy web verticle!", res.cause());
                }
                vertx.close();
            }
        });
    }

    private DeploymentOptions getDeploymentOptions(BeanFactory beanFactory) throws BeansException {
        DeploymentOptions options;
        try {
            options = beanFactory.getSpecificBean(SERVER_OPTIONS_NAME, DeploymentOptions.class);

            if (log.isInfoEnabled()) {
                log.info("server options find, will use it's options!");
            }

        } catch (NoSuchBeanException e) {

            if (log.isWarnEnabled()) {
                log.warn("no server options find, so will use default options, " +
                        "but we recommend you using customer options!");
            }

            options = new DeploymentOptions();

        }
        return options;
    }

    @Override
    protected void registerProcessor() throws BeansException {
        super.registerProcessor();
        try {
            ClassLoader.getSystemClassLoader().loadClass("cn.bdqfork.cache.processor.CacheSupportProcessor");
            if (log.isInfoEnabled()) {
                log.info("cache support!");
            }
        } catch (ClassNotFoundException e) {
            if (log.isDebugEnabled()) {
                log.debug("no cache support!");
            }
            return;
        }
        ResourceReader resourceReader = getBean(ResourceReader.class);
        boolean cacheEnable = resourceReader.readProperty(CacheProperty.CACHE_ENABLE, Boolean.class, false);
        if (cacheEnable) {
            if (log.isInfoEnabled()) {
                log.info("enable cache!");
            }
            BeanDefinition beanDefinition = BeanDefinition.builder()
                    .beanName("cacheSupportProcessor")
                    .beanClass(CacheSupportProcessor.class)
                    .scope(BeanDefinition.SINGLETON)
                    .build();
            getBeanFactory().registerBeanDefinition(beanDefinition.getBeanName(), beanDefinition);
        }
    }

    @Override
    protected void registerProxyProcessorBean() throws BeansException {
        super.registerProxyProcessorBean();
        BeanDefinition beanDefinition = BeanDefinition.builder()
                .beanName("verticleProxyProcessor")
                .beanClass(VerticleProxyProcessor.class)
                .scope(BeanDefinition.SINGLETON)
                .build();
        getBeanFactory().registerBeanDefinition(beanDefinition.getBeanName(), beanDefinition);
    }

    @Override
    protected void registerBean() throws BeansException {
        super.registerBean();
        registerVertx();
        registerRouter();
        registerWebServer();
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

    @Override
    protected void doClose() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        vertx.close(res -> {
            if (res.succeeded()) {
                if (log.isInfoEnabled()) {
                    log.info("closed vertx!");
                }
            }
            latch.countDown();
        });
        latch.await();
        super.doClose();
    }

}
