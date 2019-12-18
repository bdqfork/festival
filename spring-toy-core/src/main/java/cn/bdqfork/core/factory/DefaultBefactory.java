package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.*;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;
import cn.bdqfork.core.util.BeanUtils;
import cn.bdqfork.core.util.ReflectUtils;

import javax.inject.Provider;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019/12/15
 */
public class DefaultBefactory extends AbstractAutoInjectedBeanFactory implements BeanDefinitionRegistry {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private BeanFactory parentBeanFactory;

    protected Object instantiate(Constructor<?> constructor, Object[] args) throws BeansException {
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new FailedInjectedConstructorException(e);
        }
    }

    @Override
    protected Object autoInjectedConstructor(String beanName, BeanDefinition beanDefinition, Constructor<?> constructor, Object[] explicitArgs) throws BeansException {
        if (explicitArgs != null) {
            return instantiate(constructor, explicitArgs);
        }
        MultInjectedPoint multInjectedPoint = beanDefinition.getInjectedConstructor();
        Class<?> beanClass = beanDefinition.getBeanClass();
        if (multInjectedPoint == null) {
            multInjectedPoint = new MultInjectedPoint();
        }
        try {
            constructor = beanClass.getConstructor(multInjectedPoint.getActualTypes());
        } catch (NoSuchMethodException e) {
            throw new FailedInjectedConstructorException(e);
        }
        explicitArgs = resovleMultDependence(multInjectedPoint, beanName);
        return instantiate(constructor, explicitArgs);
    }

    @Override
    protected void autoInjectedField(String beanName, BeanDefinition beanDefinition, Object instance) throws BeansException {
        for (Map.Entry<String, InjectedPoint> pointEntry : beanDefinition.getInjectedFields().entrySet()) {
            Class<?> beanClass = beanDefinition.getBeanClass();
            Field field;
            try {
                field = beanClass.getDeclaredField(pointEntry.getKey());
            } catch (NoSuchFieldException e) {
                throw new FailedInjectedFieldException(e);
            }
            injectedField(beanName, instance, field, pointEntry.getValue());
        }
    }

    protected void injectedField(String beanName, Object instance, Field field, InjectedPoint injectedPoint) throws BeansException {
        Object value = resovleDependence(injectedPoint, beanName);
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new FailedInjectedFieldException(e);
        }
    }

    @Override
    protected void autoInjectedMethod(String beanName, BeanDefinition beanDefinition, Object instance) throws BeansException {
        for (Map.Entry<String, MultInjectedPoint> pointEntry : beanDefinition.getInjectedSetters().entrySet()) {
            Class<?> beanClass = beanDefinition.getBeanClass();
            MultInjectedPoint multInjectedPoint = pointEntry.getValue();
            Method method;
            try {
                method = beanClass.getDeclaredMethod(pointEntry.getKey(), multInjectedPoint.getClassTypes());
            } catch (NoSuchMethodException e) {
                throw new FailedInjectedMethodException(e);
            }
            injectedMethod(beanName, instance, method, multInjectedPoint);
        }
    }

    @Override
    protected Object doResovleDependence(String name, Type type, boolean check) throws BeansException {
        Object bean;
        Class<?> actualType = ReflectUtils.getActualType(type);
        if ("".equals(name)) {
            bean = getBean(actualType);
        } else {
            bean = getBean(name);
        }
        if (bean == null && check) {
            throw new UnsatisfiedBeanException(String.format("there is no bean named %s or type of %s", name, type.getTypeName()));
        }
        if (BeanUtils.isProvider(type)) {
            Object finalBean = bean;
            bean = (Provider<Object>) () -> finalBean;
        }
        return bean;
    }

    protected void injectedMethod(String beanName, Object instance, Method method, MultInjectedPoint multInjectedPoint) throws BeansException {
        Object[] args = resovleMultDependence(multInjectedPoint, beanName);
        method.setAccessible(true);
        try {
            method.invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new FailedInjectedMethodException(e);
        }
    }


    @Override
    protected boolean containBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeansException {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefination(String beanName) {
        return beanDefinitionMap.get(beanName);
    }

    @Override
    public Map<String, BeanDefinition> getBeanDefinations() {
        return beanDefinitionMap;
    }

    @Override
    protected <T> BeanDefinition getBeanDefination(Class<T> clazz) {
        for (BeanDefinition beanDefinition : beanDefinitionMap.values()) {
            if (BeanUtils.checkIsInstance(beanDefinition.getBeanClass(), clazz)) {
                return beanDefinition;
            }
        }
        return null;
    }

    @Override
    public void setParentBeanFactory(BeanFactory beanFactory) {
        parentBeanFactory = beanFactory;
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return parentBeanFactory;
    }

    @Override
    public <T> Map<String, T> getBeans(Class<T> clazz) throws BeansException {
        Map<String, T> map = new HashMap<>();
        for (BeanDefinition beanDefinition : beanDefinitionMap.values()) {
            if (beanDefinition.getBeanClass() == clazz) {
                String beanName = beanDefinition.getBeanName();
                T bean = getBean(beanName);
                map.put(beanName, bean);
            }
        }
        return map;
    }
}
