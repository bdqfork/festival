package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.UnsatisfiedBeanException;
import cn.bdqfork.core.factory.registry.DefaultSingletonBeanRegistry;
import cn.bdqfork.core.exception.BeansException;

/**
 * @author bdq
 * @since 2019/12/15
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {

    @Override
    public <T> T getBean(String beanName) throws BeansException {
        return getBean(beanName, null);
    }

    @Override
    public <T> T getBean(Class<T> clazz) throws BeansException {
        BeanDefinition beanDefinition = getBeanDefination(clazz);
        if (beanDefinition == null) {
            //todo: info
            throw new BeansException("");
        }
        return getBean(beanDefinition.getBeanName());
    }

    @Override
    public <T> T getBean(String beanName, Object[] args) throws BeansException {
        return doGetBean(beanName, args);
    }

    @SuppressWarnings("unchecked")
    protected <T> T doGetBean(String beanName, Object[] args) throws BeansException {
        Object bean = getSingleton(beanName);
        if (bean != null && args == null) {
            return (T) bean;
        }
        if (!containBean(beanName)) {
            throw new BeansException("");
        }

        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        if (beanDefinition == null) {
            throw new BeansException("");
        }
        for (String dependOn : beanDefinition.getDependOns()) {
            if (isDependent(dependOn, beanName)) {
                throw new UnsatisfiedBeanException("circular dependency exists !");
            }
            getBean(dependOn);
        }
        if (isSingleton(beanName)) {
            bean = getSingleton(beanName, () -> {
                try {
                    return createBean(beanName, beanDefinition, args);
                } catch (BeansException e) {
                    throw new IllegalStateException(e);
                }
            });
        } else if (isPrototype(beanName)) {
            bean = createBean(beanName, beanDefinition, args);
        } else {
            //todo:异常信息
            throw new BeansException("");
        }
        return (T) bean;
    }

    protected BeanDefinition getBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = getBeanDefination(beanName);
        if (beanDefinition == null) {
            BeanFactory beanFactory = getParentBeanFactory();
            if (beanFactory != null) {
                beanDefinition = getBeanDefination(beanName);
            }
        }
        return beanDefinition;
    }

    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException;

    @Override
    public boolean containBean(String beanName) {
        if (containSingleton(beanName) || containBeanDefinition(beanName)) {
            return true;
        }
        BeanFactory beanFactory = getParentBeanFactory();
        return (beanFactory != null) && beanFactory.containBean(beanName);
    }

    protected abstract boolean containBeanDefinition(String beanName);

    @Override
    public boolean isSingleton(String beanName) throws BeansException {
        BeanDefinition beanDefinition = getBeanDefination(beanName);
        if (beanDefinition == null) {
            //todo: info
            throw new BeansException("");
        }
        return beanDefinition.isSingleton();
    }

    @Override
    public boolean isPrototype(String beanName) throws BeansException {
        BeanDefinition beanDefinition = getBeanDefination(beanName);
        if (beanDefinition == null) {
            //todo: info
            throw new BeansException("");
        }
        return beanDefinition.isPrototype();
    }

    protected abstract BeanDefinition getBeanDefination(String beanName);

    protected abstract <T> BeanDefinition getBeanDefination(Class<T> clazz);

}
