package cn.bdqfork.context.aware;

import cn.bdqfork.core.exception.BeansException;

/**
 * @author bdq
 * @since 2020/1/16
 */
public interface ClassLoaderAware {
    void setClassLoader(ClassLoader classLoader) throws BeansException;
}
