package cn.bdqfork.core.factory.registry;

/**
 * @author bdq
 * @since 2019/12/16
 */
public interface SingletonBeanRegistry extends Registry {

    /**
     * 注册实例
     * @param beanName
     * @param bean
     */
    void registerSingleton(String beanName, Object bean);

    /**
     * 获取实例
     * @param beanName
     * @return
     */
    Object getSingleton(String beanName);

    /**
     * 删除实例
     * @param beanName
     */
    void removeSingleton(String beanName);

    /**
     * 是否包含实例
     * @param beanName
     * @return
     */
    boolean containSingleton(String beanName);

}
