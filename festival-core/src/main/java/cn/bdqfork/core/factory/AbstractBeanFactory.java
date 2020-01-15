package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.CircularDependencyException;
import cn.bdqfork.core.exception.NoSuchBeanException;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;
import cn.bdqfork.core.factory.registry.DefaultSingletonBeanRegistry;
import cn.bdqfork.core.util.BeanUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author bdq
 * @since 2019/12/15
 */
@Slf4j
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory, BeanDefinitionRegistry {

    @Override
    public <T> T getBean(String beanName) throws BeansException {
        return getBean(beanName, null);
    }

    @Override
    public <T> T getBean(Class<T> clazz) throws BeansException {
        List<BeanDefinition> beanDefinitions = getBeanDefinitions(clazz);
        if (beanDefinitions == null || !(beanDefinitions.size() > 0)) {
            throw new NoSuchBeanException(String.format("there is no such bean of class %s !", clazz.getCanonicalName()));
        } else if (beanDefinitions.size() > 1) {
            throw new BeansException(String.format("there is more than one bean of class %s !", clazz.getCanonicalName()));
        }
        return getBean(beanDefinitions.get(0).getBeanName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getSpecificBean(String beanName, Class<T> clazz) throws BeansException {
        Object bean = getBean(beanName);
        if (BeanUtils.checkIsInstance(bean.getClass(), clazz)) {
            return (T) bean;
        } else {
            throw new NoSuchBeanException(String.format("there is no such bean of class %s !", clazz.getCanonicalName()));
        }
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
        if (underCreatingOrDestroying(beanName)) {
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
            if (log.isTraceEnabled()) {
                log.trace("get singleton of type {} !", beanDefinition.getBeanClass().getName());
            }
            bean = getSingleton(beanName, () -> {
                try {
                    return createBean(beanName, beanDefinition, args);
                } catch (BeansException e) {
                    destroySingleton(beanName);
                    throw new IllegalStateException(e);
                }
            });
        } else if (isPrototype(beanName)) {
            if (log.isTraceEnabled()) {
                log.trace("get prototype of type {} !", beanDefinition.getBeanClass().getName());
            }
            bean = createBean(beanName, beanDefinition, args);

        } else {
            throw new BeansException(String.format("illegal scope %s !", beanDefinition.getScope()));
        }
        return (T) bean;
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

    @Override
    public boolean isSingleton(String beanName) throws BeansException {
        if (!containBean(beanName)) {
            throw new NoSuchBeanException(String.format("no such bean named %s !", beanName));
        }
        return getBeanDefinition(beanName).isSingleton();
    }

    @Override
    public boolean isPrototype(String beanName) throws BeansException {
        if (!containBean(beanName)) {
            throw new NoSuchBeanException(String.format("there is no such bean named %s !", beanName));
        }
        return getBeanDefinition(beanName).isPrototype();
    }

    public void destroySingletons() {
        if (log.isTraceEnabled()) {
            log.trace("destroy singletons !");
        }
        for (String singletonName : getSingletonNames()) {
            Object singleton = getSingleton(singletonName);
            preDestory(singletonName, singleton);
            destroySingleton(singletonName);
        }
    }

    protected void preDestory(String singletonName, Object singleton) {
        if (singleton instanceof DisposableBean) {
            if (log.isDebugEnabled()) {
                log.debug("invoke dispose method for bean {} !", singletonName);
            }
            DisposableBean disposableBean = (DisposableBean) singleton;
            try {
                disposableBean.destroy();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

}
