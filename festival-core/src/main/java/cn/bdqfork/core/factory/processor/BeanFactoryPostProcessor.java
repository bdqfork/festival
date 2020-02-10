package cn.bdqfork.core.factory.processor;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;

/**
 * bean工厂后置处理器自定义接口，功能交给用户来实现
 *
 * @author bdq
 * @since 2020/1/7
 */
public interface BeanFactoryPostProcessor {
    /**
     * 执行后置处理
     *
     * @param beanFactory 可配置bean工厂
     * @throws BeansException 异常
     */
    void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException;
}
