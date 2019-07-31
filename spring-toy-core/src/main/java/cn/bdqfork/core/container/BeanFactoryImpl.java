package cn.bdqfork.core.container;

import cn.bdqfork.core.annotation.ScopeType;
import cn.bdqfork.core.exception.*;
import cn.bdqfork.core.proxy.ProxyFactory;
import cn.bdqfork.core.utils.BeanUtils;

import javax.inject.Provider;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bdq
 * @since 2019-07-30
 */
public class BeanFactoryImpl implements BeanFactory {
    private Map<String, BeanDefinition> beanDefinitions;
    private Map<String, Class> instantiatingFlag;
    private Map<String, Object> instances;

    public BeanFactoryImpl() {
        beanDefinitions = new HashMap<>();
        instantiatingFlag = new HashMap<>();
        instances = new HashMap<>();
    }

    /**
     * 将Bean描述注册到容器中
     *
     * @param beanName       Bean名称
     * @param beanDefinition Bean的描述信息
     * @throws ConflictedBeanException Bean冲突异常
     */
    @Override
    public void register(String beanName, BeanDefinition beanDefinition) throws ConflictedBeanException {
        if (beanDefinitions.containsKey(beanName)) {
            throw new ConflictedBeanException(String.format("the entity named %s has conflicted ! ", beanName));
        }
        beanDefinitions.put(beanName, beanDefinition);
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        BeanDefinition beanDefinition = beanDefinitions.get(beanName);
        if (beanDefinition == null) {
            return null;
        }
        Object target;
        if (ScopeType.SINGLETON.equals(beanDefinition.getScope())) {
            instantiateIfNeed(beanName, beanDefinition);
            target = instances.get(beanName);

            if (target != null && ScopeType.SINGLETON.equals(beanDefinition.getScope())) {
                if (beanDefinition.isLazy()) {
                    processField(beanName, beanDefinition);
                    processMethod(beanName, beanDefinition);
                }
            }
        } else {
            UnSharedInstance unSharedInstance = new UnSharedInstance(beanDefinition.getClazz(),
                    () -> getConstructorArgs(beanName, beanDefinition));
            unSharedInstance.setObjectFactory(() -> createBean(beanName, beanDefinition, unSharedInstance.getArgs()));
            target = unSharedInstance;
        }
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        proxyFactory.setBeanFactory(this);
        proxyFactory.setInterfaces(beanDefinition.getClazz().getInterfaces());
        return proxyFactory.getProxy();
    }

    @Override
    public Object getBean(Class<?> clazz) throws BeansException {
        BeanDefinition beanDefinition = beanDefinitions.values().stream()
                .filter(definition -> BeanUtils.isSubType(definition.getClazz(), clazz) || BeanUtils.isSubType(definition.getClazz(), clazz))
                .findFirst()
                .orElse(null);
        if (beanDefinition == null) {
            return null;
        }
        return getBean(beanDefinition.getBeanName());
    }

    @Override
    public Map<String, Object> getBeans(Class<?> clazz) throws BeansException {
        Map<String, Object> proxyInstances = new HashMap<>();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition definition = entry.getValue();
            if (BeanUtils.isSubType(clazz, definition.getClazz()) || BeanUtils.isSubType(definition.getClazz(), clazz)) {
                proxyInstances.put(beanName, getBean(beanName));
            }
        }
        return proxyInstances;
    }

    @Override
    public void instantiateIfNeed(String beanName, BeanDefinition beanDefinition) throws BeansException {
        if (instances.containsKey(beanName)){
            return;
        }
        Object[] args = getConstructorArgs(beanName, beanDefinition);
        Object instance;
        ConstructorAttribute constructorAttribute = beanDefinition.getConstructorAttribute();
        if (constructorAttribute != null) {
            //执行构造器注入
            Constructor<?> constructor = constructorAttribute.getConstructor();
            try {
                instance = constructor.newInstance(args);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new InstantiateException(String.format("failed to instantiate entity %s !",
                        beanDefinition.getBeanName()), e);
            }
        } else {
            try {
                instance = beanDefinition.getClazz().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new InstantiateException(String.format("failed to instantiate entity %s !",
                        beanDefinition.getBeanName()), e);
            }
        }
        instances.put(beanName, instance);
    }

    private Object[] getConstructorArgs(String beanName, BeanDefinition beanDefinition) throws BeansException {
        ConstructorAttribute constructorAttribute = beanDefinition.getConstructorAttribute();
        Object[] args = null;
        if (constructorAttribute != null) {
            instantiatingFlag.put(beanName, beanDefinition.getClazz());
            try {
                args = getMethodArguments(beanDefinition.getConstructorAttribute().getArgs());
            } catch (BeansException e) {
                throw new BeansException(String.format("there is circular reference bean named %s !", beanName), e);
            }
            instantiatingFlag.remove(beanName);
        }
        return args;
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object instance = instances.get(beanName);
        if (instance != null && ScopeType.SINGLETON.equals(beanDefinition.getScope())) {
            if (beanDefinition.isLazy()) {
                processField(beanName, beanDefinition);
                processMethod(beanName, beanDefinition);
            }
            return instances.get(beanName);
        }
        ConstructorAttribute constructorAttribute = beanDefinition.getConstructorAttribute();
        if (constructorAttribute != null) {
            //执行构造器注入
            Constructor<?> constructor = constructorAttribute.getConstructor();
            try {
                instance = constructor.newInstance(args);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new InstantiateException(String.format("failed to instantiate entity %s !",
                        beanDefinition.getBeanName()), e);
            }
        } else {
            try {
                instance = beanDefinition.getClazz().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new InstantiateException(String.format("failed to instantiate entity %s !",
                        beanDefinition.getBeanName()), e);
            }
        }
        instances.put(beanName, instance);
        if (!ScopeType.SINGLETON.equals(beanDefinition.getScope())) {
            processField(beanName, beanDefinition);
            processMethod(beanName, beanDefinition);
        }
        return instance;
    }

    @Override
    public void processField(String beanName, BeanDefinition beanDefinition) throws BeansException {
        Object instance = instances.get(beanName);
        for (FieldAttribute fieldAttribute : beanDefinition.getFieldAttributes()) {
            Field field = fieldAttribute.getField();
            field.setAccessible(true);
            Object proxyBean = getBean(fieldAttribute.getBeanName());
            if (proxyBean == null) {
                //如果指定依赖名和默认依赖名都没有找到Bean，则按类型进行匹配
                proxyBean = getBean(fieldAttribute.getType());
            }
            if (proxyBean != null) {
                if (fieldAttribute.isProvider()) {
                    Object finalProxyBean = proxyBean;
                    proxyBean = new Provider<Object>() {
                        @Override
                        public Object get() {
                            return finalProxyBean;
                        }
                    };
                }
                try {
                    field.set(instance, proxyBean);
                } catch (IllegalAccessException e) {
                    throw new FieldInjectedException(e);
                }
            } else if (fieldAttribute.isRequired()) {
                throw new UnsatisfiedBeanException(String.format("there is no match reference bean named %s !",
                        fieldAttribute.getBeanName()));
            }
        }
    }

    @Override
    public void processMethod(String beanName, BeanDefinition beanDefinition) throws BeansException {
        Object instance = instances.get(beanName);
        for (MethodAttribute methodAttribute : beanDefinition.getMethodAttributes()) {
            Method method = methodAttribute.getMethod();
            method.setAccessible(true);
            try {
                Object[] args = getMethodArguments(methodAttribute.getArgs());
                method.invoke(instance, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                if (methodAttribute.isRequired()) {
                    throw new MethodInjectedException(String.format("failed to inject entity %s by method !",
                            beanDefinition.getBeanName()), e);
                }
            }
        }
    }

    private Object[] getMethodArguments(List<ParameterAttribute> parameterAttributes) throws BeansException {
        List<Object> args = new ArrayList<>(parameterAttributes.size());
        //遍历方法的参数依赖信息
        for (ParameterAttribute parameterAttribute : parameterAttributes) {
            boolean istantiating = isInstantiating(parameterAttribute.getBeanName(), parameterAttribute.getType());
            if (istantiating) {
                throw new UnsatisfiedBeanException(String.format("reference bean named %s is instantiating!",
                        parameterAttribute.getBeanName()));
            }
            Object proxyBean = getBean(parameterAttribute.getBeanName());
            if (proxyBean == null) {
                //如果指定依赖名和默认依赖名都没有找到Bean，则按类型进行匹配
                proxyBean = getBean(parameterAttribute.getType());
            }
            if (proxyBean == null) {
                throw new UnsatisfiedBeanException(String.format("there is no match reference bean named %s !",
                        parameterAttribute.getBeanName()));
            }
            //判断是否是Provider
            if (parameterAttribute.isProvider()) {
                //添加实例到Provider参数
                Object finalProxyBean = proxyBean;
                args.add(new Provider<Object>() {
                    @Override
                    public Object get() {
                        return finalProxyBean;
                    }
                });
                continue;
            }
            //添加代理实例作为参数
            args.add(proxyBean);
        }
        return args.toArray();
    }

    private boolean isInstantiating(String beanName, Class<?> type) {
        Class<?> clazz = instantiatingFlag.get(beanName);
        if (clazz != null) {
            return true;
        }
        for (Class<?> cls : instantiatingFlag.values()) {
            if (BeanUtils.isSubType(type, cls) || BeanUtils.isSubType(cls, type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, BeanDefinition> getBeanDefinations() {
        return this.beanDefinitions;
    }

}
