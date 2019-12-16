package cn.bdqfork.core.factory;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * bean的定义，用来描述bean的信息
 *
 * @author bdq
 * @since 2019-02-12
 */
public class BeanDefinition {
    public static final String SINGLETON = "singleton";
    public static final String PROTOTYPE = "prototype";
    /**
     * Bean的名称
     */
    private String beanName;
    /**
     * Class类型
     */
    private Class<?> beanClass;
    /**
     * Bean的作用域
     */
    private String scope;
    /**
     * 初始化必须依赖的bean
     */
    private Set<String> dependOns;
    /**
     * 构造器属性
     */
    private Constructor<?> constructor;

    /**
     * 待直接注入依赖属性
     */
    private Set<Field> fields;
    /**
     * 属性注入方法属性
     */
    private Set<Method> methods;
    /**
     * 是否已注册
     */
    private boolean isResolved;

    public BeanDefinition(String beanName, Class<?> beanClass) {
        this(beanName, beanClass, PROTOTYPE);
    }

    public BeanDefinition(String beanName, Class<?> beanClass, String scope) {
        this.beanName = beanName;
        this.beanClass = beanClass;
        this.scope = scope;
        this.dependOns = new HashSet<>();
        this.fields = new HashSet<>();
        this.methods = new HashSet<>();
    }

    public String getBeanName() {
        return beanName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public String getScope() {
        return scope;
    }

    public void addDependOn(String dependOn) {
        this.dependOns.add(dependOn);
    }

    public Set<String> getDependOns() {
        return dependOns;
    }

    public void setDependOns(Set<String> dependOns) {
        this.dependOns.addAll(dependOns);
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public Set<Method> getMethods() {
        return methods;
    }

    public void setMethods(Set<Method> methods) {
        this.methods = methods;
    }

    public boolean isSingleton() {
        return scope.equals(SINGLETON);
    }

    public boolean isPrototype() {
        return scope.equals(PROTOTYPE);
    }

    public void setResolved(boolean resolved) {
        isResolved = resolved;
    }

    public boolean isResolved() {
        return isResolved;
    }

}
