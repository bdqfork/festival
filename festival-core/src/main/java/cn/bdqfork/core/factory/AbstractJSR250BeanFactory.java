package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.definition.ManagedBeanDefinition;

/**
 * @author bdq
 * @since 2019/12/18
 */
public abstract class AbstractJSR250BeanFactory extends DefaultBeanFactory implements JSR250BeanFactory {

    @Override
    public void executePostConstuct(String beanName, Object bean) throws BeansException {
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        if (beanDefinition instanceof ManagedBeanDefinition) {
            doInitializingMethod(bean, (ManagedBeanDefinition) beanDefinition);
        }
    }

    protected abstract void doInitializingMethod(Object bean, ManagedBeanDefinition managedBeanDefinition) throws BeansException;

    @Override
    public void executePreDestroy(String beanName, Object bean) throws BeansException {
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        if (beanDefinition instanceof ManagedBeanDefinition) {
            doPreDestroyMethod(bean, (ManagedBeanDefinition) beanDefinition);
        }
    }

    protected abstract void doPreDestroyMethod(Object bean, ManagedBeanDefinition managedBeanDefinition) throws BeansException;

}
