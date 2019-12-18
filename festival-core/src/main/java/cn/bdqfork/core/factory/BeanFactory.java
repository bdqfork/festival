package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;

import java.util.Map;

/**
 * Bean工厂以及容器
 *
 * @author bdq
 * @since 2019-07-30
 */
public interface BeanFactory {

    /**
     * 根据beanName获取代理实例
     *
     * @param beanName Bean名称
     * @return Object Bean实例
     */
    <T> T getBean(String beanName) throws BeansException;

    /**
     * 根据beanName获取代理实例
     *
     * @param beanName Bean名称
     * @return Object Bean实例
     */
    <T> T getBean(String beanName, Object[] args) throws BeansException;

    /**
     * 获取第一个与clazz匹配代理实例
     *
     * @param clazz class类型
     * @return Object Bean实例
     */
    <T> T getBean(Class<T> clazz) throws BeansException;

    /**
     * 获取第一个与clazz匹配代理实例
     *
     * @param clazz class类型
     * @return Map<String, Object> Bean实例
     */
    <T> Map<String, T> getBeans(Class<T> clazz) throws BeansException;

    boolean containBean(String beanName);

    boolean isSingleton(String beanName) throws BeansException;

    boolean isPrototype(String beanName) throws BeansException;

}
