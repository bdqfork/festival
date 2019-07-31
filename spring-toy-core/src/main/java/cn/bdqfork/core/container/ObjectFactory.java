package cn.bdqfork.core.container;

import cn.bdqfork.core.exception.BeansException;

/**
 * 对象工厂
 *
 * @author bdq
 * @since 2019-02-16
 */
public interface ObjectFactory<T> {
    Object getObject() throws BeansException;
}
