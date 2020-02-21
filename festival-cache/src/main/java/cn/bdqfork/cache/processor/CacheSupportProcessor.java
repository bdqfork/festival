package cn.bdqfork.cache.processor;

import cn.bdqfork.cache.provider.CacheProvider;
import cn.bdqfork.cache.proxy.CacheInvocationHandler;
import cn.bdqfork.context.aware.BeanFactoryAware;
import cn.bdqfork.context.aware.ClassLoaderAware;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.processor.BeanPostProcessor;
import cn.bdqfork.core.proxy.javassist.Proxy;
import cn.bdqfork.core.util.AopUtils;

/**
 * @author bdq
 * @since 2020/2/21
 */
public class CacheSupportProcessor implements BeanPostProcessor, BeanFactoryAware, ClassLoaderAware {
    private ClassLoader classLoader;
    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitializtion(String beanName, Object bean) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitializtion(String beanName, Object bean) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        CacheProvider cacheProvider = beanFactory.getBean(CacheProvider.class);
        return Proxy.newProxyInstance(classLoader, targetClass.getInterfaces(), new CacheInvocationHandler(bean, cacheProvider));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setClassLoader(ClassLoader classLoader) throws BeansException {
        this.classLoader = classLoader;
    }
}
