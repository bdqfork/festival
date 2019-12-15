package cn.bdqfork.core.container;


import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.List;
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
     * 构造器属性
     */
    private ConstructorAttribute constructorAttribute;
    /**
     * 待直接注入依赖属性
     */
    private List<FieldAttribute> fieldAttributes;
    /**
     * 属性注入方法属性
     */
    private List<MethodAttribute> methodAttributes;
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

    public ConstructorAttribute getConstructorAttribute() {
        return constructorAttribute;
    }

    public void setConstructorAttribute(ConstructorAttribute constructorAttribute) {
        this.constructorAttribute = constructorAttribute;
    }

    public List<FieldAttribute> getFieldAttributes() {
        return fieldAttributes;
    }

    public void setFieldAttributes(List<FieldAttribute> fieldAttributes) {
        this.fieldAttributes = fieldAttributes;
    }

    public List<MethodAttribute> getMethodAttributes() {
        return methodAttributes;
    }

    public void setMethodAttributes(List<MethodAttribute> methodAttributes) {
        this.methodAttributes = methodAttributes;
    }

    public void setResolved(boolean resolved) {
        isResolved = resolved;
    }

    public boolean isResolved() {
        return isResolved;
    }

}
