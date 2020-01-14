package cn.bdqfork.core.factory;

import cn.bdqfork.core.factory.processor.BeanPostProcessor;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;
import cn.bdqfork.core.factory.registry.SingletonBeanRegistry;

/**
 * @author bdq
 * @since 2019/12/15
 */
public interface ConfigurableBeanFactory extends BeanFactory, SingletonBeanRegistry, BeanDefinitionRegistry {

    void setParentBeanFactory(BeanFactory beanFactory);

    BeanFactory getParentBeanFactory();

    void addPostBeanProcessor(BeanPostProcessor beanPostProcessor);

}
