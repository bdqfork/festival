package cn.bdqfork.core.container.resolver;

import cn.bdqfork.core.exception.ResolvedException;

/**
 * @author bdq
 * @since 2019-02-22
 */
public interface Resolver<T> {
    T resolve() throws ResolvedException;
}
