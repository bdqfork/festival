package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;

/**
 * @author bdq
 * @since 2019/12/18
 */
public interface JSR250BeanFactory extends ConfigurableBeanFactory {

    void executePostConstuct(String beanName, Object bean) throws BeansException;

    void executePreDestroy(String beanName, Object bean) throws BeansException;

}
