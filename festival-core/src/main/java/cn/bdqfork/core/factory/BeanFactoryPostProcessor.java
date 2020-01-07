package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;

/**
 * @author bdq
 * @since 2020/1/7
 */
public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException;
}
