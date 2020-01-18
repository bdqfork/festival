package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.definition.ManagedBeanDefinition;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象jsr250bean工厂
 * @author bdq
 * @since 2019/12/18
 */
@Slf4j
public abstract class AbstractJSR250BeanFactory extends DefaultBeanFactory implements JSR250BeanFactory {

    @Override
    public void executePostConstuct(String beanName, Object bean) throws BeansException {
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        if (beanDefinition instanceof ManagedBeanDefinition) {
            if (log.isTraceEnabled()) {
                log.trace("execute PostConstuct method for bean {} !", beanDefinition.getBeanClass().getName());
            }
            doInitializingMethod(bean, (ManagedBeanDefinition) beanDefinition);
        }
    }

    /**
     * 执行初始化方法
     * @param bean bean实例
     * @param managedBeanDefinition bean描述信息
     * @throws BeansException
     */
    protected abstract void doInitializingMethod(Object bean, ManagedBeanDefinition managedBeanDefinition) throws BeansException;

    @Override
    public void executePreDestroy(String beanName, Object bean) throws BeansException {
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        if (beanDefinition instanceof ManagedBeanDefinition) {
            if (log.isTraceEnabled()) {
                log.trace("execute PreDestroy method for bean {} !", beanDefinition.getBeanClass().getName());
            }
            doPreDestroyMethod(bean, (ManagedBeanDefinition) beanDefinition);
        }
    }

    /**
     * 执行预销毁方法
     * @param bean bean实例
     * @param managedBeanDefinition bean描述信息
     * @throws BeansException
     */
    protected abstract void doPreDestroyMethod(Object bean, ManagedBeanDefinition managedBeanDefinition) throws BeansException;

}
