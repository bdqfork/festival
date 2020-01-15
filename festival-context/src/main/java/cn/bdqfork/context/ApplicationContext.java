package cn.bdqfork.context;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.factory.ConfigurableBeanFactory;

/**
 * @author bdq
 * @since 2020/1/8
 */
public interface ApplicationContext extends BeanFactory {

    ConfigurableBeanFactory getConfigurableBeanFactory();

    void scan(String... scanPaths) throws BeansException;

    void close();
}
