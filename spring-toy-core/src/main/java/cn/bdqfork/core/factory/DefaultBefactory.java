package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.util.BeanUtils;
import cn.bdqfork.core.util.ReflectUtils;

import javax.inject.Provider;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019/12/15
 */
public class DefaultBefactory extends AbstractAutoInjectedBeanFactory implements BeanDefinitionRegistry {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private BeanFactory parentBeanFactory;

    @Override
    protected Object autoInjectedConstructor(String beanName, Constructor<?> constructor, Object[] explicitArgs) throws BeansException {
        Object[] args = null;
        if (explicitArgs != null) {
            args = explicitArgs;
        }
        return instantiate(beanName, constructor, args);
    }

    protected Object instantiate(String beanName, Constructor<?> constructor, Object[] args) throws BeansException {
        if (args == null) {
            args = resovleDependencies(new DefaultInjectedPoint(constructor), beanName);
        }
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeansException(e);
        }
    }

    @Override
    protected void autoInjectedField(String beanName, BeanDefinition beanDefinition, Object instance) throws BeansException {
        Set<Field> fields = beanDefinition.getFields();
        if (fields == null) {
            return;
        }
        for (Field field : fields) {
            injectedField(beanName, instance, field);
        }
    }

    protected void injectedField(String beanName, Object instance, Field field) throws BeansException {
        Object[] values = resovleDependencies(new DefaultInjectedPoint(field), beanName, true);
        field.setAccessible(true);
        try {
            field.set(instance, values[0]);
        } catch (IllegalAccessException e) {
            throw new BeansException(e);
        }
    }

    @Override
    protected void autoInjectedMethod(String beanName, BeanDefinition beanDefinition, Object instance) throws BeansException {
        Set<Method> methods = beanDefinition.getMethods();
        for (Method method : methods) {
            injectedMethod(beanName, instance, method);
        }
    }

    protected void injectedMethod(String beanName, Object instance, Method method) throws BeansException {
        Object[] args = resovleDependencies(new DefaultInjectedPoint(method), beanName);
        method.setAccessible(true);
        try {
            method.invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeansException(e);
        }
    }

    @Override
    protected Object[] doResovleDependencies(String[] names, Type[] types, boolean check) throws BeansException {
        Object[] objects = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            Object bean = getBean(names[i]);
            if (bean != null) {
                if (BeanUtils.isProvider(types[i])) {
                    Object finalBean = bean;
                    bean = (Provider<Object>) () -> finalBean;
                }
            }
            if (bean == null) {
                Class<?> actualType = ReflectUtils.getActualType(types[i]);
                if (BeanUtils.isProvider(types[i])) {
                    bean = (Provider<Object>) () -> {
                        try {
                            return getBean(actualType);
                        } catch (BeansException e) {
                            throw new IllegalStateException(e);
                        }
                    };
                } else {
                    bean = getBean(actualType);
                }
            }
            if (bean == null && check) {
                //todo:info
                throw new BeansException("");
            }
            if (bean != null) {
                objects[i] = bean;
            }
        }
        return objects;
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
