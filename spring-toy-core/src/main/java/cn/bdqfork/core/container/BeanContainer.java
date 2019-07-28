package cn.bdqfork.core.container;


import cn.bdqfork.core.exception.ConflictedBeanException;

import java.util.HashMap;
import java.util.Map;

/**
 * 容器类，负责管理bean
 *
 * @author bdq
 * @since 2019-02-07
 */
public class BeanContainer {
    /**
     * BeanFactory容器,key为beanName
     */
    private Map<String, BeanFactory> beans = new HashMap<>();

    /**
     * 将Bean描述注册到容器中
     *
     * @param beanName       Bean名称
     * @param beanDefinition Bean的描述信息
     * @throws ConflictedBeanException Bean冲突异常
     */
    public void register(String beanName, BeanDefinition beanDefinition) throws ConflictedBeanException {
        if (beans.containsKey(beanName)) {
            throw new ConflictedBeanException(String.format("the entity named %s has conflicted ! ", beanName));
        }
        beans.put(beanName, new BeanFactory(this, beanDefinition));
    }

    /**
     * 根据beanName获取工厂实例
     *
     * @param beanName Bean名称
     * @return BeanFactory Bean工厂实例
     */
    public BeanFactory getBean(String beanName) {
        return beans.get(beanName);
    }

    /**
     * 获取第一个与clazz匹配的工厂实例
     *
     * @param clazz Bean的类型
     * @return BeanFactory Bean工厂实例
     */
    public BeanFactory getBean(Class<?> clazz) {
        for (BeanFactory beanFactory : beans.values()) {
            if (beanFactory.getBeanDefinition().isType(clazz)) {
                return beanFactory;
            }
        }
        return null;
    }

    /**
     * 获取所有与clazz匹配的工厂实例
     *
     * @param clazz Bean的类型
     * @return BeanFactory Bean工厂实例
     */
    public Map<String, BeanFactory> getBeans(Class<?> clazz) {
        Map<String, BeanFactory> beans = new HashMap<>(8);
        for (Map.Entry<String, BeanFactory> entry : this.beans.entrySet()) {
            BeanFactory beanFactory = entry.getValue();

            BeanDefinition beanDefinition = beanFactory.getBeanDefinition();
            //匹配clazz目标类型以及目标类型的父类
            if (beanDefinition.isType(clazz) || beanDefinition.isSubType(clazz)) {
                beans.put(entry.getKey(), beanFactory);
            }
        }
        return beans;
    }

    /**
     * 获取所有工厂实例
     *
     * @return Map<String, BeanFactory>
     */
    public Map<String, BeanFactory> getAllBeans() {
        return beans;
    }

    /**
     * 获取所有bean的定义
     *
     * @return Map<String, BeanDefinition>
     */
    public Map<String, BeanDefinition> getBeanDefinations() {
        Map<String, BeanDefinition> beans = new HashMap<>(this.beans.size());
        for (Map.Entry<String, BeanFactory> entry : this.beans.entrySet()) {
            beans.put(entry.getKey(), entry.getValue().getBeanDefinition());
        }
        return beans;
    }

}
