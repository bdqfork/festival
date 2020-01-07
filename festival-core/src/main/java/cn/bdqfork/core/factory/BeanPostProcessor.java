package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;

/**
 * @author bdq
 * @since 2020/1/7
 */
public interface BeanPostProcessor {

    Object postProcessBeforeInitializtion(String beanName, Object bean) throws BeansException;

    Object postProcessAfterInitializtion(String beanName, Object bean) throws BeansException;

}
