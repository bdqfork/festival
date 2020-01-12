package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.UnsatisfiedBeanException;

/**
 * @author bdq
 * @since 2019/12/16
 */
public interface AutoInjectedBeanfactory extends ConfigurableBeanFactory {

    Object createBean(String beanName) throws BeansException;

    void autoInjected(String beanName, Object bean) throws BeansException;

    Object resovleDependence(InjectedPoint injectedPoint, String beanName) throws UnsatisfiedBeanException;

    Object[] resovleMultDependence(MultInjectedPoint multInjectedPoint, String beanName) throws UnsatisfiedBeanException;

}
