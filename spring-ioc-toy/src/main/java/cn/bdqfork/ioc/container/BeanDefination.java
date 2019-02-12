package cn.bdqfork.ioc.container;

import cn.bdqfork.ioc.generator.BeanNameGenerator;

import java.util.Map;

/**
 * @author bdq
 * @date 2019-02-12
 */
public class BeanDefination {
    private Class<?> clazz;
    private String name;
    private Object instance;
    private boolean isSingleton;
    private Map<String, Map<String, Object>> refs;

    public BeanDefination(Class<?> clazz, boolean isSingleton, String name, BeanNameGenerator generator) {
        this.clazz = clazz;
        this.isSingleton = isSingleton;
        if (name == null || "".equals(name)) {
            this.name = generator.generateBeanName(clazz);
        } else {
            this.name = name;
        }
    }

    public void setRefs(Map<String, Map<String, Object>> refs) {
        this.refs = refs;
    }

    public Map<String, Map<String, Object>> getRefs() {
        return refs;
    }

    public boolean isType(Class<?> clazz) {
        if (this.clazz == clazz) {
            return true;
        }
        Object instance = getInstance();
        return clazz.isInstance(instance);
    }

    public Object getInstance() {
        if (isSingleton) {
            if (instance == null) {
                synchronized (Object.class) {
                    if (instance == null) {
                        try {
                            instance = clazz.newInstance();
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return instance;
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getName() {
        return name;
    }
}
