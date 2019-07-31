package cn.bdqfork.core.container;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.ConflictedBeanException;

import java.util.Map;

/**
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
    void register(String beanName, BeanDefinition beanDefinition) throws ConflictedBeanException;

    /**
     * 根据beanName获取代理实例
     *
     * @param beanName Bean名称
     * @return Object Bean实例
     */
    Object getBean(String beanName) throws BeansException;

    /**
     * 获取第一个与clazz匹配代理实例
     *
     * @param clazz class类型
     * @return Object Bean实例
     */
    Object getBean(Class<?> clazz) throws BeansException;

    /**
     * 获取第一个与clazz匹配代理实例
     *
     * @param clazz class类型
     * @return Map<String, Object> Bean实例
     */
    Map<String, Object> getBeans(Class<?> clazz) throws BeansException;

    /**
     * 实例化Bean
     *
     * @param beanName       bean名称
     * @param beanDefinition BeanDefinition
     * @throws BeansException bean异常
     */
    void instantiateIfNeed(String beanName, BeanDefinition beanDefinition) throws BeansException;

    /**
     * 字段注入
     *
     * @param beanName       bean名称
     * @param beanDefinition BeanDefinition
     * @throws BeansException bean异常
     */
    void processField(String beanName, BeanDefinition beanDefinition) throws BeansException;

    /**
     * 方法注入
     *
     * @param beanName       bean名称
     * @param beanDefinition BeanDefinition
     * @throws BeansException bean异常
     */
    void processMethod(String beanName, BeanDefinition beanDefinition) throws BeansException;

    /**
     * 获取所有BeanDefinition
     *
     * @return Map<String, BeanDefinition>
     */
    Map<String, BeanDefinition> getBeanDefinations();
}
