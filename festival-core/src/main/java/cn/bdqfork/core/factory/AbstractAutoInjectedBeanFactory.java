package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
        Class<?> beanType = beanDefinition.getBeanClass();
        Constructor<?> constructor = null;
        if (explicitArgs != null) {
            Class<?>[] explicitArgTypes = Arrays.stream(explicitArgs)
                    .map(Object::getClass)
                    .toArray(Class[]::new);
            try {
                constructor = beanType.getConstructor(explicitArgTypes);
            } catch (NoSuchMethodException e) {
                throw new BeansException(e);
            }
        }
        return autoInjectedConstructor(beanName, beanDefinition, constructor, explicitArgs);
    }

    protected abstract Object autoInjectedConstructor(String beanName, BeanDefinition beanDefinition, Constructor<?> constructor, Object[] explicitArgs) throws BeansException;

    protected abstract void autoInjectedField(String beanName, BeanDefinition beanDefinition, Object instance) throws BeansException;

    protected abstract void autoInjectedMethod(String beanName, BeanDefinition beanDefinition, Object instance) throws BeansException;

    @Override
    public Object resovleDependence(InjectedPoint injectedPoint, String beanName) throws BeansException {
        if (!containBean(beanName)) {
            throw new NoSuchBeanException(String.format("there is no such bean named %s !", beanName));
        }
        return doResovleDependence(injectedPoint.getBeanName(), injectedPoint.getType(), injectedPoint.isRequire());
    }

    @Override
    public Object[] resovleMultDependence(MultInjectedPoint multInjectedPoint, String beanName) throws BeansException {
        List<Object> dependencies = new LinkedList<>();
        for (InjectedPoint injectedPoint : multInjectedPoint) {
            Object dependence = resovleDependence(injectedPoint, beanName);
            dependencies.add(dependence);
        }
        return dependencies.toArray();
    }

    protected abstract Object doResovleDependence(String name, Type type, boolean check) throws BeansException;

    @Override
    public void autoInjected(String beanName, Object bean) throws BeansException {
        BeanDefinition beanDefinition = getBeanDefination(beanName);
        autoInjectedField(beanName, beanDefinition, bean);
        autoInjectedMethod(beanName, beanDefinition, bean);
    }
}
