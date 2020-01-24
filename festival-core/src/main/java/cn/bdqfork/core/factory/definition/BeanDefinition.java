package cn.bdqfork.core.factory.definition;


import cn.bdqfork.core.factory.InjectedPoint;
import cn.bdqfork.core.factory.MultInjectedPoint;

import java.lang.reflect.Executable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
     * 构造函数/工厂方法
     */
    private Executable constructor;
    /**
     * 构造器函数/工厂方法的参数属性
     */
    private MultInjectedPoint injectedConstructor;
    /**
     * 待直接注入依赖属性
     */
    private Map<String, InjectedPoint> injectedFields;
    /**
     * 属性注入方法属性
     */
    private Map<String, InjectedPoint> injectedSetters;
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
        this.injectedFields = new HashMap<>();
        this.injectedSetters = new HashMap<>();
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

    public Executable getConstructor() {
        return constructor;
    }

    public void setConstructor(Executable constructor) {
        this.constructor = constructor;
    }

    public MultInjectedPoint getInjectedConstructor() {
        return injectedConstructor;
    }

    public void setInjectedConstructor(MultInjectedPoint injectedConstructor) {
        this.injectedConstructor = injectedConstructor;
    }

    public Map<String, InjectedPoint> getInjectedFields() {
        return injectedFields;
    }

    public void setInjectedFields(Map<String, InjectedPoint> injectedFields) {
        this.injectedFields = injectedFields;
    }

    public Map<String, InjectedPoint> getInjectedSetters() {
        return injectedSetters;
    }

    public void setInjectedSetters(Map<String, InjectedPoint> injectedSetters) {
        this.injectedSetters = injectedSetters;
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

    public static BeanDefinitionBuilder builder() {
        return new BeanDefinitionBuilder();
    }

    public static class BeanDefinitionBuilder {
        private String beanName;
        private Class<?> beanClass;
        private String scope = BeanDefinition.PROTOTYPE;
        private Set<String> dependOns = new HashSet<>();
        private Executable constructor;
        private MultInjectedPoint injectedConstructor;
        private Map<String, InjectedPoint> injectedFields = new HashMap<>();
        private Map<String, InjectedPoint> injectedSetters = new HashMap<>();
        private boolean isResolved;

        public BeanDefinitionBuilder setBeanName(String beanName) {
            this.beanName = beanName;
            return this;
        }

        public BeanDefinitionBuilder setBeanClass(Class<?> beanClass) {
            this.beanClass = beanClass;
            return this;
        }

        public BeanDefinitionBuilder setScope(String scope) {
            this.scope = scope;
            return this;
        }

        public BeanDefinitionBuilder addDependOn(String beanName) {
            this.dependOns.add(beanName);
            return this;
        }

        public BeanDefinitionBuilder setConstructor(Executable constructor) {
            this.constructor = constructor;
            return this;
        }

        public BeanDefinitionBuilder setInjectedConstructor(MultInjectedPoint injectedConstructor) {
            this.injectedConstructor = injectedConstructor;
            return this;
        }

        public BeanDefinitionBuilder addInjectedField(String fieldName, InjectedPoint injectedPoint) {
            this.injectedFields.put(fieldName, injectedPoint);
            return this;
        }

        public BeanDefinitionBuilder addInjectedSetter(String methodName, InjectedPoint injectedPoint) {
            this.injectedSetters.put(methodName, injectedPoint);
            return this;
        }

        public BeanDefinitionBuilder isResolved(boolean isResolved) {
            this.isResolved = isResolved;
            return this;
        }

        public BeanDefinition build() {
            BeanDefinition beanDefinition = new BeanDefinition(beanName, beanClass, scope);
            beanDefinition.setDependOns(dependOns);
            beanDefinition.setConstructor(constructor);
            beanDefinition.setInjectedConstructor(injectedConstructor);
            beanDefinition.setInjectedFields(injectedFields);
            beanDefinition.setInjectedSetters(injectedSetters);
            beanDefinition.setResolved(true);
            return beanDefinition;
        }
    }
}
