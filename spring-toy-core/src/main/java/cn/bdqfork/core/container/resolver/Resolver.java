package cn.bdqfork.core.container.resolver;

import cn.bdqfork.core.exception.ResolvedException;

/**
 * 解析器
 *
 * @author bdq
 * @since 2019-02-22
 */
public interface Resolver<T> {
    /**
     * 解析
     *
     * @return T
     * @throws ResolvedException 解析异常
     */
    T resolve() throws ResolvedException;
}
