package cn.bdqfork.core.container;

import cn.bdqfork.core.exception.BeansException;

import java.lang.reflect.Member;
import java.util.Map;

/**
 * @author bdq
 * @since 2019/12/15
 */
public interface ConfigurableBeanFactory extends BeanFactory {
    /**
     * 获取所有BeanDefinition
     *
     * @return Map<String, BeanDefinition>
     */
    BeanDefinition getBeanDefination(String beanName);

    /**
     * 获取所有BeanDefinition
     *
     * @return Map<String, BeanDefinition>
     */
    BeanDefinition getBeanDefination(Class<?> classType);

    /**
     * 获取所有BeanDefinition
     *
     * @return Map<String, BeanDefinition>
     */
    Map<String, BeanDefinition> getBeanDefinations();

    Object createBean(String beanName) throws BeansException;

    Object resovleDependency(Class<?> classType, String name) throws BeansException;

}
