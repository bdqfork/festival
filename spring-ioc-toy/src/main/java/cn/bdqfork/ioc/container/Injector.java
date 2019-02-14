package cn.bdqfork.ioc.container;

import cn.bdqfork.ioc.exception.InjectedException;
import cn.bdqfork.ioc.exception.SpringToyException;

/**
 * @author bdq
 * @date 2019-02-14
 */
public interface Injector {
    /**
     * 判断当前bean是否依赖beanDefination，如果是，返回true，否则返回false
     *
     * @param beanDefination
     * @return boolean
     */
    boolean hasDependence(BeanDefination beanDefination);

    /**
     * 注入依赖
     *
     * @param instance
     * @param beanDefination
     * @return
     * @throws InjectedException
     */
    Object inject(Object instance, BeanDefination beanDefination) throws InjectedException;
}
