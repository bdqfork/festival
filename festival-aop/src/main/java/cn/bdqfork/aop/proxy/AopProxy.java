package cn.bdqfork.aop.proxy;

/**
 * @author bdq
 * @since 2020/1/14
 */
public interface AopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);

}
