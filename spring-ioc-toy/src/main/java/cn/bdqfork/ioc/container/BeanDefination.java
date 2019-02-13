package cn.bdqfork.ioc.container;

import cn.bdqfork.ioc.exception.SpringToyException;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * bean的定义，用来描述bean的信息
 *
 * @author bdq
 * @date 2019-02-12
 */
public class BeanDefination {
    private Class<?> clazz;
    private String name;
    private Object instance;
    private boolean isSingleton;
    private Map<String, DependenceData> dependenceDataMap;

    public BeanDefination(Class<?> clazz, boolean isSingleton, String name) {
        this.clazz = clazz;
        this.isSingleton = isSingleton;
        this.name = name;
    }

    /**
     * 判断当前bean是否是clazz的类型，如果是，返回true，否则返回false
     *
     * @param clazz
     * @return boolean
     */
    public boolean isType(Class<?> clazz) {
        if (this.clazz == clazz) {
            return true;
        }
        return clazz.isAssignableFrom(this.clazz);
    }

    /**
     * 获取对象实例，如果bean是单例的，则每次都返回同一个实例，如果不是，则每次都创建一个新的实例。
     *
     * @return Object
     */
    public Object getInstance() throws SpringToyException {
        if (isSingleton) {
            return getSingleInstance();
        }
        return newBean();
    }

    private Object getSingleInstance() throws SpringToyException {
        if (instance == null) {
            synchronized (Object.class) {
                if (instance == null) {
                    instance = newBean();
                }
            }
        }
        return instance;
    }

    private Object newBean() throws SpringToyException {
        try {
            Object instance = clazz.newInstance();
            doInject(instance);
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SpringToyException("failed to init bean : " + name, e);
        }
    }

    private void doInject(Object instance) throws IllegalAccessException, SpringToyException {
        for (DependenceData dependenceData : dependenceDataMap.values()) {
            BeanDefination bean = dependenceData.getBean();
            Field field = dependenceData.getField();
            field.setAccessible(true);
            field.set(instance, bean.getInstance());
        }
    }

    /**
     * 判断当前bean是否依赖beanDefination，如果是，返回true，否则返回false
     *
     * @param beanDefination
     * @return boolean
     */
    public boolean hasDependence(BeanDefination beanDefination) {
        for (DependenceData dependenceData : dependenceDataMap.values()) {
            return dependenceData.isMatch(beanDefination);
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setDependenceDataMap(Map<String, DependenceData> dependenceDataMap) {
        this.dependenceDataMap = dependenceDataMap;
    }

    public Map<String, DependenceData> getDependenceDataMap() {
        return dependenceDataMap;
    }

}
