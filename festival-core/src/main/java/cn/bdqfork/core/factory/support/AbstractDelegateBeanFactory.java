package cn.bdqfork.core.factory.support;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;

import java.util.Map;

/**
 * @author bdq
 * @since 2019/12/19
 */
public abstract class AbstractDelegateBeanFactory implements ConfigurableBeanFactory {
    @Override
    public <T> T getBean(String beanName) throws BeansException {
        return getParentBeanFactory().getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, Object[] args) throws BeansException {
        return getParentBeanFactory().getBean(beanName, args);
    }

    @Override
    public <T> T getBean(Class<T> clazz) throws BeansException {
        return getParentBeanFactory().getBean(clazz);
    }

    @Override
    public <T> T getSpecificBean(String beanName, Class<T> clazz) throws BeansException {
        return getParentBeanFactory().getSpecificBean(beanName,clazz);
    }

    @Override
    public <T> Map<String, T> getBeans(Class<T> clazz) throws BeansException {
        return getParentBeanFactory().getBeans(clazz);
    }

    @Override
    public boolean containBean(String beanName) {
        return getParentBeanFactory().containBean(beanName);
    }

    @Override
    public boolean isSingleton(String beanName) throws BeansException {
        return getParentBeanFactory().isSingleton(beanName);
    }

    @Override
    public boolean isPrototype(String beanName) throws BeansException {
        return getParentBeanFactory().isPrototype(beanName);
    }
}
