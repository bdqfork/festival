package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/19
 */
public class DefaultJSR250BeanFactory extends AbstractJSR250BeanFactory {

    @Override
    protected void afterPropertiesSet(String beanName, Object bean) throws BeansException {
        super.afterPropertiesSet(beanName, bean);
        executePostConstuct(beanName, bean);
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
    protected void preDestory(String singletonName, Object singleton) {
        super.preDestory(singletonName, singleton);
        try {
            executePreDestroy(singletonName, singleton);
        } catch (BeansException e) {
            throw new IllegalStateException(e);
        }
    }
}
