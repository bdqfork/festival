package cn.bdqfork.core.factory.definition;

/**
 * @author bdq
 * @since 2020/1/15
 */
public class BeanDefinitionBuilder {
    private String beanName;
    private Class<?> beanClass;
    private String scope = BeanDefinition.PROTOTYPE;

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

    public BeanDefinition build() {
        return new BeanDefinition(beanName, beanClass, scope);
    }
}
