package cn.bdqfork.core.factory.registry;

/**
 * 实现SingletonBeanRegistry接口的类，需要实现对单实例的bean进行管理的方法。
 * 对于非单例的bean，不需要进行管理。
 *
 * @author bdq
 * @since 2019/12/16
 */
public interface SingletonBeanRegistry extends Registry {

    /**
     * 注册一个实例，beanName应该具有唯一性，不同注册相同的实例。
     *
     * @param beanName bean的名称
     * @param bean     bean实例
     */
    void registerSingleton(String beanName, Object bean);

    /**
     * 获取一个实例
     *
     * @param beanName bean的名称
     * @return 实例
     */
    Object getSingleton(String beanName);

    /**
     * 删除一个实例
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
