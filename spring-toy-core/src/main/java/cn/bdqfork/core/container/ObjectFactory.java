package cn.bdqfork.core.container;

import javax.inject.Provider;

/**
 * 对象工厂
 *
 * @author bdq
 * @date 2019-02-16
 */
public interface ObjectFactory<T> extends Provider<T> {
}
