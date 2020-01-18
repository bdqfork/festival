package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.BeansException;

/**
 * 遵循jsr250的bean工厂
 * @author bdq
 * @since 2019/12/18
 */
public interface JSR250BeanFactory extends ConfigurableBeanFactory {

    /**
     * 执行预生成方法
     *
     * @param beanName bean名称
     * @param bean bean对象
     * @throws BeansException
     */
    void executePostConstuct(String beanName, Object bean) throws BeansException;

    /**
     * 执行预销毁方法
     *
     * @param beanName bean名称
     * @param bean bean对象
     * @throws BeansException
     */
    void executePreDestroy(String beanName, Object bean) throws BeansException;

}
