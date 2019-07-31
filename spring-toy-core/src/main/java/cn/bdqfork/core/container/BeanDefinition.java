package cn.bdqfork.core.container;


import java.util.List;
import java.util.Objects;

/**
 * bean的定义，用来描述bean的信息
 *
 * @author bdq
 * @since 2019-02-12
 */
public class BeanDefinition {
    /**
     * Class类型
     */
    private Class<?> clazz;
    /**
     * Bean的名称
     */
    private String name;
    /**
     * Bean的作用域
     */
    private String scope;
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
     * 是否延迟初始化
     */
    private boolean isLazy;
    /**
     * 是否已注册
     */
    private boolean isResolved;

    public BeanDefinition(Class<?> clazz, String scope, String name, boolean isLazy) {
        this.clazz = clazz;
        this.scope = scope;
        this.name = name;
        this.isLazy = isLazy;
    }

    public String getBeanName() {
        return name;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getScope() {
        return scope;
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

    public boolean isLazy() {
        return isLazy;
    }

    public void setResolved(boolean resolved) {
        isResolved = resolved;
    }

    public boolean isResolved() {
        return isResolved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BeanDefinition that = (BeanDefinition) o;
        return clazz.equals(that.clazz) &&
                name.equals(that.name) &&
                scope.equals(that.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, name, scope);
    }
}
