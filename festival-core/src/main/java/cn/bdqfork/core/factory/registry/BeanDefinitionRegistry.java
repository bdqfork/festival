package cn.bdqfork.core.factory.registry;

import cn.bdqfork.core.factory.BeanDefinition;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.ConflictedBeanException;

import java.util.List;
import java.util.Map;

/**
 * @author bdq
 * @since 2019/12/16
 */
public interface BeanDefinitionRegistry extends Registry {

    boolean isSingleton(String beanName) throws BeansException;

    boolean isPrototype(String beanName) throws BeansException;

    /**
     * 将Bean描述注册到容器中
     *
     * @param beanName       Bean名称
     * @param beanDefinition Bean的描述信息
     * @throws ConflictedBeanException Bean冲突异常
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeansException;

    /**
     * 获取所有BeanDefinition
     *
     * @return Map<String, BeanDefinition>
     */
    BeanDefinition getBeanDefinition(String beanName);

    /**
     * 获取所有BeanDefinition
     *
     * @return Map<String, BeanDefinition>
     */
    List<BeanDefinition> getBeanDefinitions(Class<?> beanType);

    /**
     * 获取所有BeanDefinition
     *
     * @return Map<String, BeanDefinition>
     */
    Map<String, BeanDefinition> getBeanDefinitions();
}
