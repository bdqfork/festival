package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.*;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;
import cn.bdqfork.core.util.BeanUtils;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.core.util.StringUtils;

import javax.inject.Provider;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019/12/15
 */
public class DefaultBeanFactory extends AbstractAutoInjectedBeanFactory {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private BeanFactory parentBeanFactory;

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

    protected Object instantiate(Constructor<?> constructor, Object[] args) throws BeansException {
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new FailedInjectedConstructorException(e);
        }
    }

    @Override
    protected void doInjectedField(String beanName, Object instance, Field field, InjectedPoint injectedPoint) throws BeansException {
        Object value = resovleDependence(injectedPoint, beanName);
        ReflectUtils.makeAccessible(field);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new FailedInjectedFieldException(e);
        }
    }


    @Override
    protected void doInjectedMethod(String beanName, Object instance, Method method, InjectedPoint injectedPoint) throws BeansException {
        Object arg = resovleDependence(injectedPoint, beanName);
        ReflectUtils.makeAccessible(method);
        try {
            method.invoke(instance, arg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new FailedInjectedMethodException(e);
        }
    }

    @Override
    protected Object doResovleDependence(String name, Type type, boolean check) throws BeansException {
        Object bean = null;

        if (!StringUtils.isEmpty(name) && type != null) {

            Class<?> actualType = ReflectUtils.getActualType(type);
            bean = getSpecificBean(name, actualType);

        } else if (!StringUtils.isEmpty(name)) {

            bean = getBean(name);

        } else if (type != null) {

            if (BeanUtils.isCollection(type)) {

                bean = new ArrayList<>(getBeans(ReflectUtils.getActualType(type)).values());

            } else if (BeanUtils.isMap(type)) {

                bean = getBeans(ReflectUtils.getActualType(type));

            } else {

                bean = getBean(ReflectUtils.getActualType(type));

            }

        }

        if (bean == null && check) {
            throw new UnsatisfiedBeanException(String.format("there is no bean named %s or type of %s", name, type));
        }

        bean = createIfProvider(type, bean);

        return bean;
    }

    protected Object createIfProvider(Type type, Object bean) {
        if (BeanUtils.isProvider(type)) {
            return (Provider<Object>) () -> bean;
        }
        return bean;
    }

    @Override
    protected boolean containBeanDefinition(String beanName) {
        BeanFactory beanFactory = getParentBeanFactory();
        if (beanFactory instanceof AbstractBeanFactory) {
            AbstractBeanFactory abstractBeanFactory = (AbstractBeanFactory) beanFactory;
            return beanDefinitionMap.containsKey(beanName) || abstractBeanFactory.containBeanDefinition(beanName);
        }
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeansException {
        if (containBeanDefinition(beanName)) {
            throw new ConflictedBeanException(String.format("the entity named %s has conflicted ! ", beanName));
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            BeanFactory beanFactory = getParentBeanFactory();
            if (beanFactory instanceof BeanDefinitionRegistry) {
                BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
                beanDefinition = registry.getBeanDefinition(beanName);
            }
        }
        return beanDefinition;
    }

    @Override
    public List<BeanDefinition> getBeanDefinitions(Class<?> beanType) {
        return beanDefinitionMap.values()
                .stream()
                .filter(beanDefinition -> BeanUtils.checkIsInstance(beanDefinition.getBeanClass(), beanType))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, BeanDefinition> getBeanDefinitions() {
        return beanDefinitionMap;
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
        for (BeanDefinition beanDefinition : getBeanDefinitions(clazz)) {
            String beanName = beanDefinition.getBeanName();
            T bean = getBean(beanName);
            map.put(beanName, bean);
        }
        return map;
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

}
