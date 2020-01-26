package cn.bdqfork.mvc.proxy;

import cn.bdqfork.aop.processor.AopProxyProcessor;
import cn.bdqfork.aop.proxy.javassist.Proxy;
import cn.bdqfork.context.aware.ClassLoaderAware;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.DisposableBean;
import cn.bdqfork.core.factory.processor.BeanPostProcessor;
import cn.bdqfork.core.util.AopUtils;
import cn.bdqfork.mvc.annotation.Verticle;
import cn.bdqfork.mvc.context.ServiceVerticle;
import cn.bdqfork.mvc.processer.VertxAware;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

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
            vertx.deployVerticle(verticle);
            return Proxy.newProxyInstance(classLoader, targetClass.getInterfaces(), new VerticleProxyHandler(vertx, targetClass));
        }
        return bean;
    }

    @Override
    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void setClassLoader(ClassLoader classLoader) throws BeansException {
        this.classLoader = classLoader;
    }

}
