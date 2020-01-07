package cn.bdqfork.core.factory;

/**
 * @author bdq
 * @since 2020/1/7
 */
public interface DisposableBean {
    void destroy() throws Exception;
}
