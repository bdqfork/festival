package cn.bdqfork.core.container;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.util.BeanUtils;

import java.lang.reflect.Constructor;
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
public abstract class AbstractBeanFactory implements ConfigurableBeanFactory {
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    protected Map<String, Object> instances = new ConcurrentHashMap<>(256);

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeansException {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public boolean containBean(String beanName) {
        return getBeanDefination(beanName) != null;
    }

    @Override
    public boolean isSingleton(String beanName) {
        return getBeanDefination(beanName).getScope().equals(BeanDefinition.SINGLETON);
    }

    @Override
    public boolean isPrototype(String beanName) {
        return getBeanDefination(beanName).getScope().equals(BeanDefinition.PROTOTYPE);
    }

    @Override
    public <T> T getBean(String beanName) throws BeansException {
        return doGetBean(beanName);
    }

    @SuppressWarnings("unchecked")
    protected <T> T doGetBean(String beanName) throws BeansException {
        Object bean = getSingleton(beanName);
        if (bean != null) {
            return (T) bean;
        }
        if (!containBean(beanName)) {
            throw new BeansException("");
        }

        BeanDefinition beanDefinition = getBeanDefination(beanName);
        for (String dependOn : beanDefinition.getDependOns()) {
            if (isDependent(dependOn, beanName)) {
                throw new BeansException("");
            }
            getBean(dependOn);
        }
        if (BeanDefinition.SINGLETON.equals(beanDefinition.getScope())) {
            bean = createBean(beanName);
            instances.put(beanName, bean);
        } else if (BeanDefinition.PROTOTYPE.equals(beanDefinition.getScope())) {
            bean = createBean(beanName);
        } else {
            //todo:异常信息
            throw new BeansException("");
        }
        return (T) bean;
    }

    protected abstract boolean isDependent(String dependOn, String beanName);

    protected abstract Object getSingleton(String beanName);

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
    public <T> Map<String, T> getBeans(Class<T> clazz) throws BeansException {
        List<BeanDefinition> beanDefinitions = getBeanDefinations().values().stream()
                .filter(beanDefinition -> BeanUtils.checkIsInstance(clazz, beanDefinition.getBeanClass()))
                .collect(Collectors.toList());
        Map<String, T> map = new HashMap<>();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            map.put(beanDefinition.getBeanName(), getBean(beanDefinition.getBeanName()));
        }
        return map;
    }

    @Override
    public BeanDefinition getBeanDefination(String beanName) {
        return beanDefinitionMap.get(beanName);
    }

    @Override
    public BeanDefinition getBeanDefination(Class<?> classType) {
        return beanDefinitionMap.values().stream()
                .filter(beanDefinition -> BeanUtils.checkIsInstance(classType, beanDefinition.getBeanClass()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Map<String, BeanDefinition> getBeanDefinations() {
        return beanDefinitionMap;
    }

    @Override
    public Object createBean(String beanName) throws BeansException {
        BeanDefinition beanDefinition = getBeanDefination(beanName);
        ConstructorAttribute constructorAttribute = beanDefinition.getConstructorAttribute();
        if (constructorAttribute == null) {
            try {
                constructorAttribute = new ConstructorAttribute(beanDefinition.getBeanClass().getDeclaredConstructor());
            } catch (NoSuchMethodException e) {
                throw new BeansException(e);
            }
        }
        List<Object> args = new ArrayList<>(constructorAttribute.getArgs().size());
        for (ParameterAttribute parameterAttribute : constructorAttribute.getArgs()) {
            Object arg = resovleDependency(parameterAttribute.getType(), parameterAttribute.getBeanName());
            args.add(arg);
        }
        return doCreateBean(beanName, constructorAttribute.getConstructor(), args.toArray());
    }

    protected abstract Object doCreateBean(String beanName, Constructor<?> constructor, Object[] args) throws BeansException;

    @Override
    public Object resovleDependency(Class<?> classType, String name) throws BeansException {
        Object bean = getBean(name);
        if (bean == null) {
            bean = getBean(classType);
        }
        return bean;
    }
}
