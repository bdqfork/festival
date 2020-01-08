package cn.bdqfork.core.context;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;

import java.util.Map;

/**
 * @author bdq
 * @since 2020/1/8
 */
public abstract class AbstractApplicationContext implements ApplicationContext {

    private ConfigurableBeanFactory beanFactory;

    public AbstractApplicationContext(ConfigurableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public <T> T getBean(String beanName) throws BeansException {
        return beanFactory.getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, Object[] args) throws BeansException {
        return beanFactory.getBean(beanName, args);
    }

    @Override
    public <T> T getBean(Class<T> clazz) throws BeansException {
        return beanFactory.getBean(clazz);
    }

    @Override
    public <T> T getSpecificBean(String beanName, Class<T> clazz) throws BeansException {
        return beanFactory.getSpecificBean(beanName, clazz);
    }

    @Override
    public <T> Map<String, T> getBeans(Class<T> clazz) throws BeansException {
        return beanFactory.getBeans(clazz);
    }

    @Override
    public boolean containBean(String beanName) {
        return beanFactory.containBean(beanName);
    }

    @Override
    public boolean isSingleton(String beanName) throws BeansException {
        return beanFactory.isSingleton(beanName);
    }

    @Override
    public boolean isPrototype(String beanName) throws BeansException {
        return beanFactory.isPrototype(beanName);
    }

    public ConfigurableBeanFactory getConfigurableBeanFactory() {
        return beanFactory;
    }
}
