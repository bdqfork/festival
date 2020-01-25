package cn.bdqfork.aop.proxy;

import java.util.List;

/**
 * @author bdq
 * @since 2020/1/14
 */
public class AopProxyConfig {

    private Class<?> beanClass;

    private Object bean;

    private List<Class<?>> interfaces;

    private boolean optimze;

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public List<Class<?>> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<Class<?>> interfaces) {
        this.interfaces = interfaces;
    }

    public boolean isOptimze() {
        return optimze;
    }

    public void setOptimze(boolean optimze) {
        this.optimze = optimze;
    }

}
