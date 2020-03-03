package cn.bdqfork.core.proxy;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/3/3
 */
public interface InvokerAware {
    Object doInvoke(Object proxy, Method method, Object[] args)
            throws Throwable;
}
