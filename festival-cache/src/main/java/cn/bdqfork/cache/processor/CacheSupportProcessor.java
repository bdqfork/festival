package cn.bdqfork.cache.processor;

import cn.bdqfork.cache.constant.CacheProperty;
import cn.bdqfork.cache.provider.CacheProvider;
import cn.bdqfork.cache.provider.RedisCacheProvider;
import cn.bdqfork.cache.proxy.CacheInvocationHandler;
import cn.bdqfork.context.aware.BeanFactoryAware;
import cn.bdqfork.context.aware.ClassLoaderAware;
import cn.bdqfork.context.aware.ResourceReaderAware;
import cn.bdqfork.context.configuration.reader.ResourceReader;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.processor.BeanPostProcessor;
import cn.bdqfork.core.proxy.javassist.Proxy;
import cn.bdqfork.core.util.AopUtils;

/**
 * @author bdq
 * @since 2020/2/21
 */
public class CacheSupportProcessor implements BeanPostProcessor, BeanFactoryAware, ClassLoaderAware, ResourceReaderAware {
    private ClassLoader classLoader;
    private ResourceReader resourceReader;
    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitializtion(String beanName, Object bean) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitializtion(String beanName, Object bean) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        String cacheType = resourceReader.readProperty(CacheProperty.CACHE_TYPE, String.class, "");
        CacheProvider cacheProvider;
        if (cacheType.equals(CacheProperty.REDIS_CACHE_TYPE)) {
            try {
                cacheProvider = beanFactory.getBean(RedisCacheProvider.class);
            } catch (NoSuchBeanException e) {
                throw new IllegalStateException(String.format("no cache provider of %s found!", cacheType), e);
            }
        } else {
            throw new IllegalStateException("should have assigned value for cache type,but not found!");
        }
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

    @Override
    public void setResourceReader(ResourceReader resourceReader) throws BeansException {
        this.resourceReader = resourceReader;
    }
}
