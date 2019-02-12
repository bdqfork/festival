package cn.bdqfork.ioc.container;

import cn.bdqfork.ioc.exception.SpringToyException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bdq
 * @date 2019-02-07
 */
public class BeanContainer {
    private Map<String, BeanDefination> beans = new HashMap<>();

    public void register(String beanName, BeanDefination beanDefination) throws SpringToyException {
        if (beans.containsKey(beanName)) {
            throw new SpringToyException("there are two bean has same name ! ");
        }
        beans.put(beanName, beanDefination);
    }

    public Object getBean(String beanName) {
        BeanDefination beanDefination = beans.get(beanName);
        return beanDefination.getInstance();
    }

    public <T> T getBean(Class<T> clazz) {
        for (BeanDefination beanDefination : beans.values()) {
            if (beanDefination.isType(clazz)) {
                return (T) beanDefination.getInstance();
            }
        }
        return null;
    }

    public <T> Map<String, T> getBeans(Class<T> clazz) {
        Map<String, T> beans = new HashMap<>(8);
        for (Map.Entry<String, BeanDefination> entry : this.beans.entrySet()) {
            BeanDefination beanDefination = entry.getValue();
            if (beanDefination.isType(clazz)) {
                beans.put(entry.getKey(), (T) beanDefination.getInstance());
            }
        }
        return beans;
    }

    public Map<String, BeanDefination> getBeanDefinations() {
        return beans;
    }
}
