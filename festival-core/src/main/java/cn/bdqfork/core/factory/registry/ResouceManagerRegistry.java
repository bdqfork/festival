package cn.bdqfork.core.factory.registry;

import cn.bdqfork.core.exception.BeansException;

/**
 * @author bdq
 * @since 2019/12/18
 */
public interface ResouceManagerRegistry extends Registry {
    /**
     * 注册ManagedBean
     *
     * @param name
     * @param managedBean
     * @throws BeansException
     */
    void registerResouceManager(String name, Object managedBean) throws BeansException;


}
