package cn.bdqfork.core.context;

import cn.bdqfork.core.container.BeanNameGenerator;
import cn.bdqfork.core.exception.BeansException;

import java.util.Map;

/**
 * 应用上下文
 *
 * @author bdq
 * @date 2019-02-13
 */
public interface ApplicationContext {

    /**
     * 根据beanName获取实例
     *
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object getBean(String beanName) throws BeansException;

    /**
     * 获取第一个与clazz匹配的实例
     *
     * @param clazz
     * @param <T>
     * @return
     * @throws BeansException
     */
    <T> T getBean(Class<T> clazz) throws BeansException;

    /**
     * 获取所有与clazz匹配的实例
     *
     * @param clazz
     * @param <T>
     * @return
     * @throws BeansException
     */
    <T> Map<String, T> getBeans(Class<T> clazz) throws BeansException;

    void setBeanNameGenerator(BeanNameGenerator beanNameGenerator);
}
