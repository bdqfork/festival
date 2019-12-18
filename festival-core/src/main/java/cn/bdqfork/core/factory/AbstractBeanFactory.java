package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.CircularDependencyException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.registry.DefaultSingletonBeanRegistry;

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
            throw new NoSuchBeanException(String.format("there is no such bean of class %s !", clazz.getCanonicalName()));
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
        if (isCreating(beanName)) {
            throw new BeansException(String.format("bean named %s is under creating !", beanName));
        }

        if (!containBean(beanName)) {
            throw new NoSuchBeanException(String.format("there is no such bean named %s !", beanName));
        }

        BeanDefinition beanDefinition = getBeanDefinition(beanName);

        for (String dependOn : beanDefinition.getDependOns()) {
            if (isDependent(dependOn, beanName)) {
                throw new CircularDependencyException("circular dependency exists !");
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
            throw new BeansException("unsupport scope !");
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
        if (!containBean(beanName)) {
            throw new NoSuchBeanException(String.format("no such bean named %s !", beanName));
        }
        return getBeanDefination(beanName).isSingleton();
    }

    @Override
    public boolean isPrototype(String beanName) throws BeansException {
        if (!containBean(beanName)) {
            throw new NoSuchBeanException(String.format("there is no such bean named %s !", beanName));
        }
        return getBeanDefination(beanName).isPrototype();
    }

    protected abstract BeanDefinition getBeanDefination(String beanName);

    protected abstract <T> BeanDefinition getBeanDefination(Class<T> clazz);

}
