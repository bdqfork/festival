package cn.bdqfork.context.aware;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanFactory;

/**
 * @author bdq
 * @since 2020/1/21
 */
public interface BeanFactoryAware {
    void setBeanFactory(BeanFactory beanFactory) throws BeansException;
}
