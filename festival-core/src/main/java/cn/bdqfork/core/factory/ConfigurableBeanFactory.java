package cn.bdqfork.core.factory;

import cn.bdqfork.core.factory.processor.BeanPostProcessor;

/**
 * @author bdq
 * @since 2019/12/15
 */
public interface ConfigurableBeanFactory extends BeanFactory {

    void setParentBeanFactory(BeanFactory beanFactory);

    BeanFactory getParentBeanFactory();

    void addPostBeanProcessor(BeanPostProcessor beanPostProcessor);

}
