package cn.bdqfork.core.container;


import cn.bdqfork.core.exception.ConflictedBeanException;
import cn.bdqfork.core.exception.SpringToyException;

import java.util.HashMap;
import java.util.Map;

/**
 * 容器类，负责管理bean
 *
 * @author bdq
 * @date 2019-02-07
 */
public class BeanContainer {
    private Map<String, BeanDefinition> beans = new HashMap<>();

    public void register(String beanName, BeanDefinition beanDefinition) throws ConflictedBeanException {
        if (beans.containsKey(beanName)) {
            throw new ConflictedBeanException(String.format("the entity named: %s has conflicted ! ", beanName));
        }
        beans.put(beanName, beanDefinition);
    }

    /**
     * 根据beanName获取实例
     *
     * @param beanName
     * @return
     * @throws SpringToyException
     */
    public Object getBean(String beanName) throws SpringToyException {
        BeanDefinition beanDefinition = beans.get(beanName);
        return beanDefinition.getInstance();
    }

    /**
     * 获取第一个与clazz匹配的实例
     *
     * @param clazz
     * @return
     * @throws SpringToyException
     */
    public BeanDefinition getBean(Class<?> clazz) throws SpringToyException {
        for (BeanDefinition beanDefinition : beans.values()) {
            if (beanDefinition.isType(clazz)) {
                return beanDefinition;
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
    public Map<String, BeanDefinition> getBeans(Class<?> clazz) throws SpringToyException {
        Map<String, BeanDefinition> beans = new HashMap<>(8);
        for (Map.Entry<String, BeanDefinition> entry : this.beans.entrySet()) {
            BeanDefinition beanDefinition = entry.getValue();
            if (beanDefinition.isType(clazz) || beanDefinition.isSubType(clazz)) {
                beans.put(entry.getKey(), beanDefinition);
            }
        }
        return beans;
    }

    /**
     * 获取所有bean的定义
     *
     * @return
     */
    public Map<String, BeanDefinition> getBeanDefinations() {
        return beans;
    }

}
