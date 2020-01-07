package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.NoSuchBeanException;

/**
 * @author bdq
 * @since 2019/12/18
 */
public abstract class AbstractJSR250BeanFactory extends DefaultBeanFactory implements JSR250BeanFactory {

    @Override
    public void executePostConstuct(String beanName, Object bean) {
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        if (beanDefinition instanceof ManagedBeanDefinition) {
            try {
                doInitializingMethod(bean, (ManagedBeanDefinition) beanDefinition);
            } catch (BeansException e) {
                throw new IllegalStateException(e);
            }
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
