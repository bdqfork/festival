package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;

/**
 * @author bdq
 * @since 2019/12/16
 */
public interface AutoInjectedBeanfactory extends ConfigurableBeanFactory {

    Object createBean(String beanName) throws BeansException;

    void autoInjected(String beanName, Object bean) throws BeansException;

    Object[] resovleDependencies(InjectedPoint injectedPoint, String beanName) throws BeansException;

    Object[] resovleDependencies(InjectedPoint injectedPoint, String beanName, boolean check) throws BeansException;
}
