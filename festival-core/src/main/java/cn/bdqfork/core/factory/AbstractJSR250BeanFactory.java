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
        if (managedBeanDefinition == null) {
            //todo:info
            throw new NoSuchBeanException("");
        }
        doInitializingMethod(bean, managedBeanDefinition);
    }

    protected ManagedBeanDefinition getManagedBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        ManagedBeanDefinition managedBeanDefinition = null;
        if (beanDefinition instanceof ManagedBeanDefinition) {
            managedBeanDefinition = (ManagedBeanDefinition) beanDefinition;
        }
        return managedBeanDefinition;
    }

    protected abstract void doInitializingMethod(Object bean, ManagedBeanDefinition managedBeanDefinition) throws BeansException;

    @Override
    public void executePreDestroy(String beanName, Object bean) throws BeansException {
        ManagedBeanDefinition managedBeanDefinition = getManagedBeanDefinition(beanName);
        if (managedBeanDefinition == null) {
            //todo:info
            throw new NoSuchBeanException("");
        }
        doPreDestroyMethod(bean, managedBeanDefinition);
    }

    protected abstract void doPreDestroyMethod(Object bean, ManagedBeanDefinition managedBeanDefinition) throws BeansException;

}
