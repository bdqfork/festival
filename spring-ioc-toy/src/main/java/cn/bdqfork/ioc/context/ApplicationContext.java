package cn.bdqfork.ioc.context;

import cn.bdqfork.ioc.exception.SpringToyException;
import cn.bdqfork.ioc.generator.BeanNameGenerator;

import java.util.Map;

/**
 * 应用上下文
 *
 * @author bdq
 * @date 2019-02-13
 */
public interface ApplicationContext {

    /**
     * 根据beanName获取实例
     *
     * @param beanName
     * @return
     * @throws SpringToyException
     */
    Object getBean(String beanName) throws SpringToyException;

    /**
     * 获取第一个与clazz匹配的实例
     *
     * @param clazz
     * @param <T>
     * @return
     * @throws SpringToyException
     */
    <T> T getBean(Class<T> clazz) throws SpringToyException;

    /**
     * 获取所有与clazz匹配的实例
     *
     * @param clazz
     * @param <T>
     * @return
     * @throws SpringToyException
     */
    <T> Map<String, T> getBeans(Class<T> clazz) throws SpringToyException;

    void setBeanNameGenerator(BeanNameGenerator beanNameGenerator);
}
