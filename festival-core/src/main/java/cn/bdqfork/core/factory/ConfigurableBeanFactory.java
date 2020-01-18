package cn.bdqfork.core.factory;

import cn.bdqfork.core.factory.processor.BeanPostProcessor;
import cn.bdqfork.core.factory.registry.BeanDefinitionRegistry;
import cn.bdqfork.core.factory.registry.SingletonBeanRegistry;

/**
 * 可配置的Bean工厂
 * @author bdq
 * @since 2019/12/15
 */
public interface ConfigurableBeanFactory extends BeanFactory, SingletonBeanRegistry, BeanDefinitionRegistry {

    /**
     * 设置委托工厂
     *
     * @param beanFactory bean工厂
     */
    void setParentBeanFactory(BeanFactory beanFactory);

    /**
     * 获取委托工厂
     *
     * @return BeanFactory
     */
    BeanFactory getParentBeanFactory();

    /**
     * 添加Bean后置处理器
     *
     * @param beanPostProcessor
     */
    void addPostBeanProcessor(BeanPostProcessor beanPostProcessor);

}
