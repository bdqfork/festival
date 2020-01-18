package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.UnsatisfiedBeanException;

/**
 * 实现自动注入Bean工厂
 *
 * @author bdq
 * @since 2019/12/16
 */
public interface AutoInjectedBeanfactory extends ConfigurableBeanFactory {

    /**
     * 生成bean对象
     *
     * @param beanName bean名称
     * @return Object bean实例
     * @throws BeansException
     */
    Object createBean(String beanName) throws BeansException;

    /**
     * 自动注入
     *
     * @param beanName bean名称
     * @param bean bean实例
     * @throws BeansException
     */
    void autoInjected(String beanName, Object bean) throws BeansException;

    /**
     * 解决依赖
     * @param injectedPoint 注入点实例
     * @param beanName bean名称
     * @return 完成注入的bean对象
     * @throws UnsatisfiedBeanException 根据bean名称无法找到bean
     */
    Object resovleDependence(InjectedPoint injectedPoint, String beanName) throws UnsatisfiedBeanException;

    /**
     * 解决多重依赖
     * @param multInjectedPoint 多重依赖注入点
     * @param beanName bean名称
     * @return 完成注入的bean对象数组
     * @throws UnsatisfiedBeanException
     */
    Object[] resovleMultDependence(MultInjectedPoint multInjectedPoint, String beanName) throws UnsatisfiedBeanException;

}
