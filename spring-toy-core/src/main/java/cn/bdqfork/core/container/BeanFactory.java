package cn.bdqfork.core.container;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.ConflictedBeanException;

import java.util.Map;

/**
 * Bean工厂以及容器
 *
 * @author bdq
 * @since 2019-07-30
 */
public interface BeanFactory {

    /**
     * 将Bean描述注册到容器中
     *
     * @param beanName       Bean名称
     * @param beanDefinition Bean的描述信息
     * @throws ConflictedBeanException Bean冲突异常
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeansException;

    boolean containBean(String beanName);

    boolean isSingleton(String beanName);

    boolean isPrototype(String beanName);

    /**
     * 根据beanName获取代理实例
     *
     * @param beanName Bean名称
     * @return Object Bean实例
     */
    <T> T getBean(String beanName) throws BeansException;

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

}
