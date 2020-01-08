package cn.bdqfork.core.context;

import cn.bdqfork.core.exception.BeansException;

import java.util.Map;

/**
 * @author bdq
 * @since 2020/1/8
 */
public abstract class AbstractApplicationContext implements ApplicationContext {

    @Override
    public <T> T getBean(String beanName) throws BeansException {
        return getConfigurableBeanFactory().getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, Object[] args) throws BeansException {
        return getConfigurableBeanFactory().getBean(beanName, args);
    }

    @Override
    public <T> T getBean(Class<T> clazz) throws BeansException {
        return getConfigurableBeanFactory().getBean(clazz);
    }

    @Override
    public <T> T getSpecificBean(String beanName, Class<T> clazz) throws BeansException {
        return getConfigurableBeanFactory().getSpecificBean(beanName, clazz);
    }

    @Override
    public <T> Map<String, T> getBeans(Class<T> clazz) throws BeansException {
        return getConfigurableBeanFactory().getBeans(clazz);
    }

    @Override
    public boolean containBean(String beanName) {
        return getConfigurableBeanFactory().containBean(beanName);
    }

    @Override
    public boolean isSingleton(String beanName) throws BeansException {
        return getConfigurableBeanFactory().isSingleton(beanName);
    }

    @Override
    public boolean isPrototype(String beanName) throws BeansException {
        return getConfigurableBeanFactory().isPrototype(beanName);
    }
}
