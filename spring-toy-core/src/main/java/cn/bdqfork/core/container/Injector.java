package cn.bdqfork.core.container;

import cn.bdqfork.core.exception.InjectedException;

/**
 * @author bdq
 * @date 2019-02-14
 */
public interface Injector {
    /**
     * 判断当前bean是否依赖beanDefination，如果是，返回true，否则返回false
     *
     * @param beanDefinition
     * @return boolean
     */
    boolean hasDependence(BeanDefinition beanDefinition);

    /**
     * 注入依赖
     *
     * @param instance
     * @param beanDefinition
     * @return
     * @throws InjectedException
     */
    Object inject(Object instance, BeanDefinition beanDefinition) throws InjectedException;
}
