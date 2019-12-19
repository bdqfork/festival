package cn.bdqfork.core.factory;

/**
 * @author bdq
 * @since 2019/12/19
 */
public class ManagedBeanDefinition extends BeanDefinition {
    /**
     * 初始化方法名
     */
    private String initializingMethod;
    /**
     * 销毁方法名
     */
    private String destroyMethod;

    public ManagedBeanDefinition(String beanName, Class<?> beanClass) {
        super(beanName, beanClass);
    }

    public ManagedBeanDefinition(String beanName, Class<?> beanClass, String scope) {
        super(beanName, beanClass, scope);
    }

    public String getInitializingMethod() {
        return initializingMethod;
    }

    public void setInitializingMethod(String initializingMethod) {
        this.initializingMethod = initializingMethod;
    }

    public String getDestroyMethod() {
        return destroyMethod;
    }

    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }
}
