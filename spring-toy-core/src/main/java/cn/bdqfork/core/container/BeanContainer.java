package cn.bdqfork.core.container;


import cn.bdqfork.core.exception.ConflictedBeanException;
import cn.bdqfork.core.exception.SpringToyException;

import java.util.*;

/**
 * 容器类，负责管理bean
 *
 * @author bdq
 * @date 2019-02-07
 */
public class BeanContainer {
    private Map<String, BeanFactory> beans = new HashMap<>();

    public void register(String beanName, BeanDefinition beanDefinition) throws ConflictedBeanException {
        if (beans.containsKey(beanName)) {
            throw new ConflictedBeanException(String.format("the entity named: %s has conflicted ! ", beanName));
        }
        beans.put(beanName, new BeanFactory(this, beanDefinition));
    }

    /**
     * 根据beanName获取实例
     *
     * @param beanName
     * @return
     * @throws SpringToyException
     */
    public BeanFactory getBean(String beanName) throws SpringToyException {
        return beans.get(beanName);
    }

    /**
     * 获取第一个与clazz匹配的实例
     *
     * @param clazz
     * @return
     * @throws SpringToyException
     */
    public BeanFactory getBean(Class<?> clazz) throws SpringToyException {
        for (BeanFactory beanFactory : beans.values()) {
            if (beanFactory.getBeanDefinition().isType(clazz)) {
                return beanFactory;
            }
        }
        return null;
    }

    /**
     * 获取所有与clazz匹配的实例
     *
     * @param clazz
     * @return
     * @throws SpringToyException
     */
    public Map<String, BeanFactory> getBeans(Class<?> clazz) throws SpringToyException {
        Map<String, BeanFactory> beans = new HashMap<>(8);
        for (Map.Entry<String, BeanFactory> entry : this.beans.entrySet()) {
            BeanFactory beanFactory = entry.getValue();

            BeanDefinition beanDefinition = beanFactory.getBeanDefinition();
            if (beanDefinition.isType(clazz) || beanDefinition.isSubType(clazz)) {
                beans.put(entry.getKey(), beanFactory);
            }
        }
        return beans;
    }

    /**
     * 获取所有与clazz匹配的实例
     *
     * @return
     * @throws SpringToyException
     */
    public Map<String, BeanFactory> getAllBeans() throws SpringToyException {
        return beans;
    }

    /**
     * 获取所有bean的定义
     *
     * @return
     */
    public Map<String, BeanDefinition> getBeanDefinations() {
        Map<String, BeanDefinition> beans = new HashMap<>(this.beans.size());
        for (Map.Entry<String, BeanFactory> entry : this.beans.entrySet()) {
            beans.put(entry.getKey(), entry.getValue().getBeanDefinition());
        }
        return beans;
    }

}
