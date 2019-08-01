package cn.bdqfork.core.container;

import cn.bdqfork.core.exception.BeansException;

/**
 * @author bdq
 * @since 2019-08-01
 */
public interface FactoryBean<T> {

    /**
     * 返回对象实例
     *
     * @return T 对象实例
     * @throws BeansException
     */
    T getObject() throws BeansException;


    /**
     * 返回Bean的类型
     *
     * @return Class<?> Bean的类型
     */
    Class<?> getObjectType();

    /**
     * 是否单例
     *
     * @return boolean
     */
    default boolean isSingleton() {
        return true;
    }
}
