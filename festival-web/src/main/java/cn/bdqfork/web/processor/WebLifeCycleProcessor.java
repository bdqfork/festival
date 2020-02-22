package cn.bdqfork.web.processor;

import cn.bdqfork.context.AbstractLifeCycleProcessor;
import cn.bdqfork.context.ApplicationContext;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.web.server.WebServer;
import cn.bdqfork.web.server.WebVerticle;
import cn.bdqfork.web.service.JsonMessageCodec;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @author bdq
 * @since 2020/2/22
 */
public class WebLifeCycleProcessor extends AbstractLifeCycleProcessor {
    private static final Logger log = LoggerFactory.getLogger(WebLifeCycleProcessor.class);
    public static final String SERVER_OPTIONS_NAME = "serverOptions";

    @Override
    public void beforeStart(ApplicationContext applicationContext) throws Exception {
        super.beforeStart(applicationContext);
        BeanDefinition beanDefinition = BeanDefinition.builder()
                .beanName("verticleProxyProcessor")
                .beanClass(VerticleProxyProcessor.class)
                .scope(BeanDefinition.SINGLETON)
                .build();
        applicationContext.getBeanFactory().registerBeanDefinition(beanDefinition.getBeanName(), beanDefinition);
    }

    @Override
    public void afterStart(ApplicationContext applicationContext) throws Exception {
        Vertx vertx = applicationContext.getBean(Vertx.class);
        vertx.eventBus().registerCodec(new JsonMessageCodec());

        DeploymentOptions options = getDeploymentOptions(applicationContext);

        WebServer webServer = applicationContext.getBean(WebServer.class);

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

    private DeploymentOptions getDeploymentOptions(ApplicationContext applicationContext) throws BeansException {
        DeploymentOptions options;
        try {
            options = applicationContext.getSpecificBean(SERVER_OPTIONS_NAME, DeploymentOptions.class);

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
    public void beforeStop(ApplicationContext applicationContext) throws Exception {
        Vertx vertx = applicationContext.getBean(Vertx.class);
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
    }
}
