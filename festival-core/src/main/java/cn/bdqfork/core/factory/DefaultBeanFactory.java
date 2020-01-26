package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.*;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;
import cn.bdqfork.core.util.BeanUtils;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class DefaultBeanFactory extends AbstractAutoInjectedBeanFactory {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private BeanFactory parentBeanFactory;

    @Override
    protected void afterPropertiesSet(String beanName, Object bean) throws BeansException {
        if (bean instanceof InitializingBean) {
            if (log.isTraceEnabled()) {
                log.trace("invoke InitializingBean !");
            }
            InitializingBean initializingBean = (InitializingBean) bean;
            try {
                initializingBean.afterPropertiesSet();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    protected Object autoInjectedConstructor(String beanName, BeanDefinition beanDefinition, Constructor<?> constructor, Object[] explicitArgs) throws BeansException {
        if (explicitArgs != null) {
            if (log.isTraceEnabled()) {
                log.trace("instantiate bean {} with explicit args !", beanDefinition.getBeanClass().getName());
            }
            return instantiate(constructor, explicitArgs);
        }

        if (log.isTraceEnabled()) {
            log.trace("instantiate bean {} by injected constructor !", beanDefinition.getBeanClass().getName());
        }

        MultInjectedPoint multInjectedPoint = beanDefinition.getInjectedConstructor();
        Class<?> beanClass = beanDefinition.getBeanClass();

        if (multInjectedPoint == null) {

            if (log.isTraceEnabled()) {
                log.trace("injected constructor for bean {} is null, will use default constructor !", beanDefinition.getBeanClass().getName());
            }

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
    protected Object autoInjectFactoryMethod(BeanDefinition beanDefinition) throws BeansException {

        Method factoryMethod = (Method) beanDefinition.getConstructor();

        Class<?> configBeanClass = factoryMethod.getDeclaringClass();

        MultInjectedPoint multInjectedPoint = beanDefinition.getInjectedConstructor();

        Object[] args = resovleMultDependence(multInjectedPoint, beanDefinition.getBeanName());

        Object configBean = getBean(configBeanClass);

        try {
            return ReflectUtils.invokeMethod(configBean, factoryMethod, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new BeansException(e);
        }

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
        if (log.isTraceEnabled()) {
            log.trace("do injected field {} for bean {} !", field.getName(), field.getDeclaringClass().getName());
        }
        Object value = resovleDependence(injectedPoint, beanName);
        ReflectUtils.makeAccessible(field);
        try {
            ReflectUtils.setValue(instance, field, value);
        } catch (IllegalAccessException e) {
            throw new FailedInjectedFieldException(e);
        }
    }


    @Override
    protected void doInjectedMethod(String beanName, Object instance, Method method, InjectedPoint injectedPoint) throws BeansException {
        if (log.isTraceEnabled()) {
            log.trace("do injected method {} for bean {} !", method.getName(), method.getDeclaringClass().getName());
        }
        Object arg = resovleDependence(injectedPoint, beanName);
        try {
            ReflectUtils.invokeMethod(instance, method, arg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new FailedInjectedMethodException(e);
        }
    }

    @Override
    protected Object doResovleDependence(String name, Type type, boolean check) throws UnsatisfiedBeanException {
        Object bean = null;
        try {
            if (!StringUtils.isEmpty(name) && type != null) {
                Class<?> actualType = (Class<?>) ReflectUtils.getActualType(type)[0];

                if (log.isTraceEnabled()) {
                    log.trace("resovle dependence by name {} and type {} !", name, actualType.getTypeName());
                }

                bean = getSpecificBean(name, actualType);

            } else if (!StringUtils.isEmpty(name)) {

                if (log.isTraceEnabled()) {
                    log.trace("resovle dependence by name {} !", name);
                }

                bean = getBean(name);

            } else if (type != null) {

                Type[] actualTypes = ReflectUtils.getActualType(type);

                if (BeanUtils.isCollection(type)) {

                    Class<?> actualType = (Class<?>) actualTypes[0];

                    if (log.isTraceEnabled()) {
                        log.trace("resovle collection dependences by type {} !", actualType.getTypeName());
                    }

                    bean = new ArrayList<>(getBeans(actualType).values());

                } else if (BeanUtils.isMap(type)) {

                    Class<?> actualType = (Class<?>) actualTypes[1];

                    if (log.isTraceEnabled()) {
                        log.trace("resovle map dependences by type {} !", actualType.getTypeName());
                    }

                    bean = getBeans(actualType);

                } else {
                    Class<?> actualType = (Class<?>) actualTypes[0];

                    if (log.isTraceEnabled()) {
                        log.trace("resovle object dependence by type {} !", actualType.getTypeName());
                    }

                    bean = getBean(actualType);

                }

            }
        } catch (BeansException e) {
            throw new UnsatisfiedBeanException(e);
        }

        if (bean == null && check) {
            throw new UnsatisfiedBeanException(String.format("there is no bean named %s or type of %s", name, type));
        }

        bean = createIfProvider(type, bean);

        return bean;
    }

    protected Object createIfProvider(Type type, Object bean) {
        if (BeanUtils.isProvider(type)) {
            if (log.isTraceEnabled()) {
                log.trace("create provider for {} !", type.getTypeName());
            }
            return (Provider<Object>) () -> bean;
        }
        return bean;
    }

    @Override
    public boolean containBeanDefinition(String beanName) {
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
            if (log.isTraceEnabled()) {
                log.trace("try to get BeanDefinition from parent factory !");
            }
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
    public void clearAllBeanDefinitions() {
        beanDefinitionMap.clear();
        if (getParentBeanFactory() instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) getParentBeanFactory();
            registry.clearAllBeanDefinitions();
        }
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
