package cn.bdqfork.context;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;

/**
 * 应用前后关系
 * 包含beanFactory的所有功能之外还提供了自动扫描资源目录的功能
 *
 * @author bdq
 * @since 2020/1/8
 */
public interface ApplicationContext extends BeanFactory {

    /**
     * 获取可配置bean工厂
     *
     * @return 可配置bean工厂
     */
    ConfigurableBeanFactory getConfigurableBeanFactory();

    /**
     * 扫描指定路径，自动将其添加到容器，并依赖注入
     *
     * @param scanPaths
     * @throws BeansException
     */
    void scan(String... scanPaths) throws BeansException;

    /**
     * 销毁容器中的单例类
     */
    void close() throws Exception;

    boolean isClosed();
}
