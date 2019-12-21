package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;

/**
 * @author bdq
 * @since 2019/12/18
 */
public abstract class AbstractJSR250BeanFactory extends DefaultBeanFactory implements JSR250BeanFactory {

    @Override
    public void executePostConstuct(String beanName, Object bean) throws BeansException {
        ManagedBeanDefinition managedBeanDefinition = getManagedBeanDefinition(beanName);
        doInitializingMethod(bean, managedBeanDefinition);
    }

    protected ManagedBeanDefinition getManagedBeanDefinition(String beanName) throws NoSuchBeanException {
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        if (beanDefinition instanceof ManagedBeanDefinition) {
            return (ManagedBeanDefinition) beanDefinition;
        } else {
            throw new NoSuchBeanException(String.format("managed bean %s is not exist !", beanName));
        }
    }

    protected abstract void doInitializingMethod(Object bean, ManagedBeanDefinition managedBeanDefinition) throws BeansException;

    @Override
    public void executePreDestroy(String beanName, Object bean) throws BeansException {
        ManagedBeanDefinition managedBeanDefinition = getManagedBeanDefinition(beanName);
        doPreDestroyMethod(bean, managedBeanDefinition);
    }

    protected abstract void doPreDestroyMethod(Object bean, ManagedBeanDefinition managedBeanDefinition) throws BeansException;

}
