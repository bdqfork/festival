package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;

import javax.inject.Provider;
import java.lang.reflect.Constructor;

/**
 * @author bdq
 * @since 2019/12/16
 */
public abstract class AbstractAutoInjectedBeanFactory extends AbstractBeanFactory implements AutoInjectedBeanfactory {
    @Override
    public Object createBean(String beanName) throws BeansException {
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        return createBean(beanName, beanDefinition, null);
    }

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        return doCreateBean(beanName, beanDefinition, args);
    }

    protected Object doCreateBean(String beanName, BeanDefinition beanDefinition, Object[] explicitArgs) throws BeansException {
        registerCreatingSingleton(beanName, () -> {
            try {
                return createInstance(beanName, beanDefinition, explicitArgs);
            } catch (BeansException e) {
                throw new IllegalStateException(e);
            }
        });
        Object bean = getSingleton(beanName, true);
        autoInjected(beanName, bean);
        registerSingleton(beanName, bean);
        return bean;
    }

    private Object createInstance(String beanName, BeanDefinition beanDefinition, Object[] explicitArgs) throws BeansException {
        Constructor<?> constructor = beanDefinition.getConstructor();
        if (constructor == null) {
            Class<?> beanClass = beanDefinition.getBeanClass();
            try {
                constructor = beanClass.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new BeansException("");
            }
            beanDefinition.setConstructor(constructor);
        }
        return autoInjectedConstructor(beanName, constructor, explicitArgs);
    }

    protected abstract Object autoInjectedConstructor(String beanName, Constructor<?> constructor, Object[] explicitArgs) throws BeansException;

    protected abstract void autoInjectedField(String beanName, BeanDefinition beanDefinition, Object instance) throws BeansException;

    protected abstract void autoInjectedMethod(String beanName, BeanDefinition beanDefinition, Object instance) throws BeansException;

    @Override
    public Object[] resovleDependencies(InjectedPoint injectedPoint, String beanName) throws BeansException {
        return resovleDependencies(injectedPoint, beanName, true);
    }

    @Override
    public Object[] resovleDependencies(InjectedPoint injectedPoint, String beanName, boolean check) throws BeansException {
        if (!containBean(beanName)) {
            throw new BeansException("");
        }
        String[] names = injectedPoint.getInjectedNames();
        Class<?>[] types = injectedPoint.getInjectedTypes();
        return doResovleDependencies(names, types, check);
    }

    protected abstract Object[] doResovleDependencies(String[] names, Class<?>[] types, boolean check) throws BeansException;

    @Override
    public void autoInjected(String beanName, Object bean) throws BeansException {
        BeanDefinition beanDefinition = getBeanDefination(beanName);
        autoInjectedField(beanName, beanDefinition, bean);
        autoInjectedMethod(beanName, beanDefinition, bean);
    }
}
