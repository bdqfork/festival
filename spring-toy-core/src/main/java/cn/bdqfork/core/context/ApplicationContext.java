package cn.bdqfork.core.context;

import cn.bdqfork.core.container.BeanNameGenerator;
import cn.bdqfork.core.container.FactoryBean;
import cn.bdqfork.core.exception.BeansException;

import java.util.Map;

/**
 * 应用上下文
 *
 * @author bdq
 * @since 2019-02-13
 */
public interface ApplicationContext {

    /**
     * 根据beanName获取实例
     *
     * @param beanName bean名称
     * @return Object bean实例
     * @throws BeansException Bean异常
     */
    Object getBean(String beanName) throws BeansException;

    /**
     * 获取第一个与clazz匹配的实例
     *
     * @param clazz bean类型
     * @param <T>   bean泛型
     * @return T bean实例
     * @throws BeansException Bean异常
     */
    <T> T getBean(Class<T> clazz) throws BeansException;

    /**
     * 获取所有与clazz匹配的实例
     *
     * @param clazz bean类型
     * @param <T>   bean泛型
     * @return Map<String, T> bean实例以及beanName
     * @throws BeansException Bean异常
     */
    <T> Map<String, T> getBeans(Class<T> clazz) throws BeansException;

    /**
     * 注册单例Bean
     *
     * @param factoryBean 工厂Bean
     * @throws BeansException Bean异常
     */
    void registerSingleBean(FactoryBean factoryBean) throws BeansException;

    /**
     * 设置BeanName生成器
     *
     * @param beanNameGenerator BeanName生成器
     */
    void setBeanNameGenerator(BeanNameGenerator beanNameGenerator);
}
