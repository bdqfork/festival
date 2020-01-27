package cn.bdqfork.mvc.processer;

import cn.bdqfork.aop.processor.AopProxyProcessor;
import cn.bdqfork.aop.proxy.javassist.Proxy;
import cn.bdqfork.context.aware.ClassLoaderAware;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.util.AopUtils;
import cn.bdqfork.mvc.annotation.Verticle;
import cn.bdqfork.mvc.context.ServiceVerticle;
import cn.bdqfork.mvc.context.VertxAware;
import cn.bdqfork.mvc.proxy.VerticleProxyHandler;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bdq
 * @since 2020/1/26
 */
@Slf4j
public class VerticleProxyProcessor extends AopProxyProcessor implements ClassLoaderAware, VertxAware {
    private Vertx vertx;
    private ClassLoader classLoader;

    @Override
    public Object postProcessAfterInitializtion(String beanName, Object bean) throws BeansException {
        bean = super.postProcessAfterInitializtion(beanName, bean);
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (targetClass.isAnnotationPresent(Verticle.class)) {
            ServiceVerticle verticle = new ServiceVerticle(bean);
            vertx.rxDeployVerticle(verticle)
                    .subscribe(id -> {
                        if (log.isTraceEnabled()) {
                            log.trace("deployed service {} of {} by id {}!", beanName, targetClass.getCanonicalName(), id);
                        }
                    }, e -> {
                        if (log.isErrorEnabled()) {
                            log.error("failed to deploy service {} of {}!", beanName, targetClass.getCanonicalName(), e);
                        }
                        vertx.close();
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
