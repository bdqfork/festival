package cn.bdqfork.core.container;

import java.util.Map;

/**
 * @author bdq
 * @since 2019-07-30
 */
public interface FactoryBean<T> {
    /**
     * 返回的对象实例
     */
    T getObject() throws Exception;

    /**
     * Bean的类型
     *
     * @return Class<?>
     */
    Class<?> getObjectType();

    /**
     * true是单例，false是非单例  在Spring5.0中此方法利用了JDK1.8的新特性变成了default方法，返回true
     *
     * @return boolean 默认为true
     */
    default boolean isSingleton() {
        return true;
    }

}
