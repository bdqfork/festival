package cn.bdqfork.web.processor;

import cn.bdqfork.aop.processor.AopProxyProcessor;
import cn.bdqfork.aop.proxy.javassist.Proxy;
import cn.bdqfork.context.aware.ClassLoaderAware;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.util.AopUtils;
import cn.bdqfork.web.VertxAware;
import cn.bdqfork.web.annotation.VerticleMapping;
import cn.bdqfork.web.proxy.VerticleProxyHandler;
import cn.bdqfork.web.service.ServiceVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2020/1/26
 */
public class VerticleProxyProcessor extends AopProxyProcessor implements ClassLoaderAware, VertxAware {
    private static final Logger log = LoggerFactory.getLogger(VerticleProxyProcessor.class);
    private Vertx vertx;
    private ClassLoader classLoader;

    @Override
    public Object postProcessAfterInitializtion(String beanName, Object bean) throws BeansException {
        bean = super.postProcessAfterInitializtion(beanName, bean);
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (targetClass.isAnnotationPresent(VerticleMapping.class)) {
            ServiceVerticle verticle = new ServiceVerticle(bean);
            vertx.deployVerticle(verticle,res->{
                if (res.succeeded()){
                    if (log.isTraceEnabled()) {
                        log.trace("deployed service {} of {} by id {}!", beanName, targetClass.getCanonicalName(), res.result());
                    }else {
                        if (log.isErrorEnabled()) {
                            log.error("failed to deploy service {} of {}!", beanName, targetClass.getCanonicalName(), res.cause());
                        }
                        vertx.close();
                    }
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

}
