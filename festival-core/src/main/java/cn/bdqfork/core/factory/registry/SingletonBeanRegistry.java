package cn.bdqfork.core.factory.registry;

/**
 * @author bdq
 * @since 2019/12/16
 */
public interface SingletonBeanRegistry extends Registry {

    /**
     * 注册实例
     *
     * @param beanName bean的名称
     * @param bean     bean实例
     */
    void registerSingleton(String beanName, Object bean);

    /**
     * 获取实例
     *
     * @param beanName bean的名称
     * @return 实例
     */
    Object getSingleton(String beanName);

    /**
     * 删除实例
     *
     * @param beanName bean的名称
     */
    void destroySingleton(String beanName);

    /**
     * 是否包含实例
     *
     * @param beanName bean的名称
     * @return 是否包含实例
     */
    boolean containSingleton(String beanName);

}
