package cn.bdqfork.ioc.container;


/**
 * @author bdq
 * @date 2019-02-13
 */
public interface Provider<T> {
    /**
     * 获取T类型的对象实例
     *
     * @return
     */
    T getInstance();
}
