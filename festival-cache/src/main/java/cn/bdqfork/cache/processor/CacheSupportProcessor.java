package cn.bdqfork.cache.processor;

import cn.bdqfork.cache.constant.CacheProperty;
import cn.bdqfork.cache.provider.CacheProvider;
import cn.bdqfork.cache.provider.RedisCacheProvider;
import cn.bdqfork.cache.proxy.CacheInvocationHandler;
import cn.bdqfork.context.AbstractLifeCycleProcessor;
import cn.bdqfork.context.ApplicationContext;
import cn.bdqfork.context.aware.ClassLoaderAware;
import cn.bdqfork.context.configuration.reader.ResourceReader;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.processor.BeanPostProcessor;
import cn.bdqfork.core.proxy.javassist.Proxy;
import cn.bdqfork.core.util.AopUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2020/2/21
 */
public class CacheSupportProcessor extends AbstractLifeCycleProcessor implements BeanPostProcessor, ClassLoaderAware {
    private static final Logger log = LoggerFactory.getLogger(CacheSupportProcessor.class);
    private ClassLoader classLoader;
    private CacheProvider cacheProvider;
    private boolean enable;

    @Override
    public void beforeStart(ApplicationContext applicationContext) throws Exception {
        super.beforeStart(applicationContext);
        if (log.isInfoEnabled()) {
            log.info("cache supported!");
        }
    }

    @Override
    public void afterStart(ApplicationContext applicationContext) throws Exception {
        super.afterStart(applicationContext);
        ResourceReader resourceReader = applicationContext.getBean(ResourceReader.class);
        enable = resourceReader.readProperty(CacheProperty.CACHE_ENABLE, Boolean.class, false);

        String cacheType = resourceReader.readProperty(CacheProperty.CACHE_TYPE, String.class, "");
        if (cacheType.equals(CacheProperty.REDIS_CACHE_TYPE)) {
            try {
                cacheProvider = applicationContext.getBean(RedisCacheProvider.class);
            } catch (NoSuchBeanException e) {
                throw new IllegalStateException(String.format("no cache provider of %s found!", cacheType), e);
            }
        } else {
            throw new IllegalStateException("should have assigned value for cache type,but not found!");
        }
    }

    @Override
    public Object postProcessBeforeInitializtion(String beanName, Object bean) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitializtion(String beanName, Object bean) throws BeansException {
        if (!enable) {
            return bean;
        }
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        return Proxy.newProxyInstance(classLoader, targetClass.getInterfaces(), new CacheInvocationHandler(bean, cacheProvider));
    }

    @Override
    public void setClassLoader(ClassLoader classLoader) throws BeansException {
        this.classLoader = classLoader;
    }

}
