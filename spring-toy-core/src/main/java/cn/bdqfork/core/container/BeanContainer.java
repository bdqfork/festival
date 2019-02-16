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
    private Map<String, BeanDefination> beans = new HashMap<>();

    public void register(String beanName, BeanDefination beanDefination) throws ConflictedBeanException {
        if (beans.containsKey(beanName)) {
            throw new ConflictedBeanException(String.format("the bean named: %s has conflicted ! ", beanName));
        }
        beans.put(beanName, beanDefination);
    }

    /**
     * 根据beanName获取实例
     *
     * @param beanName
     * @return
     * @throws SpringToyException
     */
    public Object getBean(String beanName) throws SpringToyException {
        BeanDefination beanDefination = beans.get(beanName);
        return beanDefination.getInstance();
    }

    /**
     * 获取第一个与clazz匹配的实例
     *
     * @param clazz
     * @return
     * @throws SpringToyException
     */
    public BeanDefination getBean(Class<?> clazz) throws SpringToyException {
        for (BeanDefination beanDefination : beans.values()) {
            if (beanDefination.isType(clazz)) {
                return beanDefination;
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
    public Map<String, BeanDefination> getBeans(Class<?> clazz) throws SpringToyException {
        Map<String, BeanDefination> beans = new HashMap<>(8);
        for (Map.Entry<String, BeanDefination> entry : this.beans.entrySet()) {
            BeanDefination beanDefination = entry.getValue();
            if (beanDefination.isType(clazz) || beanDefination.isSubType(clazz)) {
                beans.put(entry.getKey(), beanDefination);
            }
        }
        return beans;
    }

    /**
     * 获取所有bean的定义
     *
     * @return
     */
    public Map<String, BeanDefination> getBeanDefinations() {
        return beans;
    }

}
