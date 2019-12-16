package cn.bdqfork.core.factory;

/**
 * @author bdq
 * @since 2019/12/15
 */
public interface ConfigurableBeanFactory extends BeanFactory {

    void setParentBeanFactory(BeanFactory beanFactory);

    BeanFactory getParentBeanFactory();
}
