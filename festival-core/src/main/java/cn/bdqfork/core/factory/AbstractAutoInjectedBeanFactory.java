package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.FailedInjectedFieldException;
import cn.bdqfork.core.exception.FailedInjectedMethodException;
import cn.bdqfork.core.exception.NoSuchBeanException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    protected Object createInstance(String beanName, BeanDefinition beanDefinition, Object[] explicitArgs) throws BeansException {
        Class<?> beanType = beanDefinition.getBeanClass();
        Constructor<?> constructor = getExplicitConstructor(beanType, explicitArgs);
        return autoInjectedConstructor(beanName, beanDefinition, constructor, explicitArgs);
    }

    protected Constructor<?> getExplicitConstructor(Class<?> beanType, Object[] explicitArgs) throws BeansException {
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
        return constructor;
    }

    protected abstract Object autoInjectedConstructor(String beanName, BeanDefinition beanDefinition, Constructor<?> constructor, Object[] explicitArgs) throws BeansException;

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
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        autoInjectedField(beanName, beanDefinition, bean);
        autoInjectedMethod(beanName, beanDefinition, bean);
    }

    protected void autoInjectedField(String beanName, BeanDefinition beanDefinition, Object instance) throws BeansException {
        for (Map.Entry<String, InjectedPoint> pointEntry : beanDefinition.getInjectedFields().entrySet()) {
            Class<?> beanClass = beanDefinition.getBeanClass();
            Field field;
            try {
                field = beanClass.getDeclaredField(pointEntry.getKey());
            } catch (NoSuchFieldException e) {
                throw new FailedInjectedFieldException(e);
            }
            doInjectedField(beanName, instance, field, pointEntry.getValue());
        }
    }

    protected abstract void doInjectedField(String beanName, Object instance, Field field, InjectedPoint value) throws BeansException;

    protected void autoInjectedMethod(String beanName, BeanDefinition beanDefinition, Object instance) throws BeansException {
        for (Map.Entry<String, InjectedPoint> pointEntry : beanDefinition.getInjectedSetters().entrySet()) {
            Class<?> beanClass = beanDefinition.getBeanClass();
            InjectedPoint injectedPoint = pointEntry.getValue();
            Method method;
            try {
                method = beanClass.getDeclaredMethod(pointEntry.getKey(), injectedPoint.getClassType());
            } catch (NoSuchMethodException e) {
                throw new FailedInjectedMethodException(e);
            }
            doInjectedMethod(beanName, instance, method, injectedPoint);
        }
    }

    protected abstract void doInjectedMethod(String beanName, Object instance, Method method, InjectedPoint injectedPoint) throws BeansException;

}
