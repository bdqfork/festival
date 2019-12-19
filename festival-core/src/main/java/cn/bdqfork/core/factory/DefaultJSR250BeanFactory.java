package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.FailedInjectedFieldException;
import cn.bdqfork.core.exception.FailedInjectedMethodException;
import cn.bdqfork.core.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/19
 */
public class DefaultJSR250BeanFactory extends AbstractJSR250BeanFactory {

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = doCreateBean(beanName, beanDefinition, args);
        executePostConstuct(beanName, bean);
        return bean;
    }

    @Override
    protected void doInitializingMethod(Object bean, ManagedBeanDefinition managedBeanDefinition) throws BeansException {
        Class<?> beanClass = managedBeanDefinition.getBeanClass();
        String methodName = managedBeanDefinition.getInitializingMethod();
        if (StringUtils.isEmpty(methodName)) {
            return;
        }
        doInvoke(bean, beanClass, methodName);
    }

    @Override
    protected void doPreDestroyMethod(Object bean, ManagedBeanDefinition managedBeanDefinition) throws BeansException {
        Class<?> beanClass = managedBeanDefinition.getBeanClass();
        String methodName = managedBeanDefinition.getDestroyMethod();
        if (StringUtils.isEmpty(methodName)) {
            return;
        }
        doInvoke(bean, beanClass, methodName);
    }

    private void doInvoke(Object bean, Class<?> beanClass, String methodName) throws BeansException {
        Method method;
        try {
            method = beanClass.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new BeansException(e);
        }
        method.setAccessible(true);
        try {
            method.invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeansException(e);
        }
    }

    @Override
    protected Object createInstance(String beanName, BeanDefinition beanDefinition, Object[] explicitArgs) throws BeansException {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Constructor<?> constructor = getExplicitConstructor(beanClass, explicitArgs);
        if (constructor == null) {
            try {
                constructor = beanClass.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new BeansException(e);
            }
        }
        return autoInjectedConstructor(beanName, beanDefinition, constructor, explicitArgs);
    }

    @Override
    protected void doInjectedField(String beanName, Object instance, Field field, InjectedPoint injectedPoint) throws BeansException {
        String refBeanName = injectedPoint.getBeanName();
        Object value;
        if (!"".equals(refBeanName)) {
            value = getBean(refBeanName);
        } else {
            refBeanName = field.getName();
            injectedPoint.setBeanName(refBeanName);
            value = resovleDependence(injectedPoint, beanName);
        }
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new FailedInjectedFieldException(e);
        }
    }

    @Override
    protected void doInjectedMethod(String beanName, Object instance, Method method, InjectedPoint injectedPoint) throws BeansException {
        String refBeanName = injectedPoint.getBeanName();
        Object arg;
        if (!"".equals(refBeanName)) {
            arg = getBean(refBeanName);
        } else {
            refBeanName = method.getName().substring(3);
            injectedPoint.setBeanName(refBeanName);
            arg = resovleDependence(injectedPoint, beanName);
        }
        method.setAccessible(true);
        try {
            method.invoke(instance, arg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new FailedInjectedMethodException(e);
        }
    }

    @Override
    public void destroySingletons() {
        for (String singletonName : getSingletonNames()) {
            Object singleton = getSingleton(singletonName);
            try {
                executePreDestroy(singletonName, singleton);
            } catch (BeansException e) {
                throw new IllegalStateException(e);
            }
            destorySingleton(singletonName);
        }
    }
}
