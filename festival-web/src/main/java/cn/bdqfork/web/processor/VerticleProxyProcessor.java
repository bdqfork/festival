package cn.bdqfork.web.processor;

import cn.bdqfork.context.aware.ClassLoaderAware;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.processor.BeanPostProcessor;
import cn.bdqfork.core.factory.processor.OrderAware;
import cn.bdqfork.core.proxy.javassist.Proxy;
import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.AopUtils;
import cn.bdqfork.web.VertxAware;
import cn.bdqfork.web.annotation.VerticleMapping;
import cn.bdqfork.web.service.ServiceVerticle;
import cn.bdqfork.web.service.VerticleProxyHandler;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2020/1/26
 */
public class VerticleProxyProcessor implements ClassLoaderAware, VertxAware, BeanPostProcessor, OrderAware {
    private static final Logger log = LoggerFactory.getLogger(VerticleProxyProcessor.class);
    private Vertx vertx;
    private ClassLoader classLoader;

    @Override
    public Object postProcessBeforeInitializtion(String beanName, Object bean) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitializtion(String beanName, Object bean) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (AnnotationUtils.isAnnotationPresent(targetClass, VerticleMapping.class)) {
            ServiceVerticle verticle = new ServiceVerticle(bean);
            vertx.deployVerticle(verticle, res -> {
                if (res.succeeded()) {
                    if (log.isDebugEnabled()) {
                        log.debug("deployed service {} of {} by id {}!", beanName, targetClass.getCanonicalName(), res.result());
                    }
                } else {
                    if (log.isErrorEnabled()) {
                        log.error("failed to deploy service {} of {}!", beanName, targetClass.getCanonicalName(), res.cause());
                    }
                    vertx.close();
                }
            });
            return Proxy.newProxyInstance(classLoader, targetClass.getInterfaces(), new VerticleProxyHandler(vertx, targetClass));
        }
        return bean;
    }

    @Override
    public void setVertx(Vertx vertx) throws BeansException {
        this.vertx = vertx;
    }

    @Override
    public void setClassLoader(ClassLoader classLoader) throws BeansException {
        this.classLoader = classLoader;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
