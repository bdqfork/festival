package cn.bdqfork.core.factory;

/**
 * 一次性的bean
 *
 * @author bdq
 * @since 2020/1/7
 */
public interface DisposableBean {
    /**
     * 销毁方法
     *
     * @throws Exception
     */
    void destroy() throws Exception;
}
