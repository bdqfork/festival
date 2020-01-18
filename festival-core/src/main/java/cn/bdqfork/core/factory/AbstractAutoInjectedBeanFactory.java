package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.*;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.processor.BeanPostProcessor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author bdq
 * @since 2019/12/16
 */
@Slf4j
public abstract class AbstractAutoInjectedBeanFactory extends AbstractBeanFactory implements AutoInjectedBeanfactory {
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>(16);

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

        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            bean = beanPostProcessor.postProcessBeforeInitializtion(beanName, bean);
        }

        afterPropertiesSet(beanName, bean);

        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            bean = beanPostProcessor.postProcessAfterInitializtion(beanName, bean);
        }

        registerSingleton(beanName, bean);

        return bean;
    }

    protected abstract void afterPropertiesSet(String beanName, Object bean) throws BeansException;

    protected Object createInstance(String beanName, BeanDefinition beanDefinition, Object[] explicitArgs) throws BeansException {
        if (log.isTraceEnabled()) {
            log.trace("create instance for {} !", beanDefinition.getBeanClass().getName());
        }
        Class<?> beanType = beanDefinition.getBeanClass();

        Constructor<?> constructor = getExplicitConstructor(beanType, explicitArgs);

        return autoInjectedConstructor(beanName, beanDefinition, constructor, explicitArgs);
    }

    protected Constructor<?> getExplicitConstructor(Class<?> beanType, Object[] explicitArgs) throws BeansException {
        Constructor<?> constructor = null;
        if (explicitArgs != null) {
            if (log.isTraceEnabled()) {
                log.trace("get explicit constructor for {} !", beanType.getName());
            }
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

    /**
     * 自动注入构造器
     * @param beanName bean名称
     * @param beanDefinition bean描述信息
     * @param constructor 要注入的构造方法
     * @param explicitArgs 构造方法参数
     * @return Object 实例化对象
     * @throws BeansException
     */
    protected abstract Object autoInjectedConstructor(String beanName, BeanDefinition beanDefinition, Constructor<?> constructor, Object[] explicitArgs) throws BeansException;

    /**
     * 解决依赖
     * @param injectedPoint 注入点实例
     * @param beanName bean名称
     * @return bean实例
     * @throws UnsatisfiedBeanException
     */
    @Override
    public Object resovleDependence(InjectedPoint injectedPoint, String beanName) throws UnsatisfiedBeanException {
        if (log.isTraceEnabled()) {
            log.trace("resolve dependence beanName {}, type {} and require is {} for bean {} with injected point !",
                    injectedPoint.getBeanName(), injectedPoint.getType().getTypeName(), injectedPoint.isRequire(), beanName);
        }
        if (!containBean(beanName)) {
            throw new UnsatisfiedBeanException(String.format("there is no such bean named %s !", beanName));
        }
        if (injectedPoint.getValue() != null) {
            if (log.isTraceEnabled()) {
                log.trace("dependence beanName {}, type {} and require is {} for bean {} exist, will return exist value !",
                        injectedPoint.getBeanName(), injectedPoint.getType().getTypeName(), injectedPoint.isRequire(), beanName);
            }
            return injectedPoint.getValue();
        }
        return doResovleDependence(injectedPoint.getBeanName(), injectedPoint.getType(), injectedPoint.isRequire());
    }

    /**
     * 解决多重依赖
     * @param multInjectedPoint 多重依赖注入点
     * @param beanName bean名称
     * @return
     * @throws UnsatisfiedBeanException
     */
    @Override
    public Object[] resovleMultDependence(MultInjectedPoint multInjectedPoint, String beanName) throws UnsatisfiedBeanException {
        List<Object> dependencies = new LinkedList<>();
        for (InjectedPoint injectedPoint : multInjectedPoint) {
            Object dependence = resovleDependence(injectedPoint, beanName);
            dependencies.add(dependence);
        }
        return dependencies.toArray();
    }

    protected abstract Object doResovleDependence(String name, Type type, boolean check) throws UnsatisfiedBeanException;

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

    @Override
    public void addPostBeanProcessor(BeanPostProcessor beanPostProcessor) {
        beanPostProcessors.add(beanPostProcessor);
    }

}
