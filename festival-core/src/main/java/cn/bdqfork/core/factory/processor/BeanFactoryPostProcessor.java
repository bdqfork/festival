package cn.bdqfork.core.factory.processor;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;

/**
 * @author bdq
 * @since 2020/1/7
 */
public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException;
}
