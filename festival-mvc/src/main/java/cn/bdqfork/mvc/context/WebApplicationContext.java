package cn.bdqfork.mvc.context;

import cn.bdqfork.context.AnnotationApplicationContext;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;
import cn.bdqfork.mvc.WebSeverRunner;
import cn.bdqfork.mvc.util.VertxUtils;
import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Slf4j
public class WebApplicationContext extends AnnotationApplicationContext {
    private Vertx vertx = VertxUtils.getVertx();

    public WebApplicationContext(String... scanPaths) throws BeansException {
        super(scanPaths);
        ConfigurableBeanFactory beanFactory = getConfigurableBeanFactory();
        WebSeverRunner runner = beanFactory.getBean(WebSeverRunner.class);
        DeploymentOptions options;
        try {
            options = beanFactory.getBean(DeploymentOptions.class);

            if (log.isInfoEnabled()) {
                log.info("DeploymentOptions find, will use it's options!");
            }

        } catch (NoSuchBeanException e) {

            if (log.isWarnEnabled()) {
                log.warn("no DeploymentOptions find, so will use default options, " +
                        "but we recommend you using customer options!");
            }

            options = new DeploymentOptions();

        }
        vertx.deployVerticle(runner, options);
    }

    @Override
    protected void registerBeanDefinition() throws BeansException {
        super.registerBeanDefinition();
        registerWebServer();
    }

    private void registerWebServer() throws BeansException {
        BeanDefinitionRegistry registry = getConfigurableBeanFactory();
        BeanDefinition beanDefinition = BeanDefinition.builder()
                .setScope(BeanDefinition.SINGLETON)
                .setBeanClass(WebSeverRunner.class)
                .setBeanName("webserver")
                .build();
        registry.registerBeanDefinition(beanDefinition.getBeanName(), beanDefinition);
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
