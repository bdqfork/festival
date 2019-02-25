package cn.bdqfork.core.container;

/**
 * @author bdq
 * @date 2019-02-13
 */
public interface InjectorData {

    /**
     * 设置注入的bean
     *
     * @param bean
     */
    void setBean(BeanDefinition bean);

    /**
     * 返回依赖的bean
     *
     * @return
     */
    BeanDefinition getBean();

    /**
     * 设置依赖的默认名称
     *
     * @param defaultName
     */
    void setDefaultName(String defaultName);

    /**
     * 获取依赖的默认名称
     *
     * @return
     */
    String getDefaultName();

    /**
     * 获取指定的依赖的名称
     *
     * @return
     */
    String getRefName();

    /**
     * 获取依赖的类型
     *
     * @return
     */
    Class<?> getType();

    /**
     * 判断依赖是否匹配
     *
     * @param beanDefinition
     * @return
     */
    boolean isMatch(BeanDefinition beanDefinition);

    /**
     * 是否必须
     *
     * @return
     */
    boolean isRequired();

    /**
     * 设置是否是注入器
     *
     * @param provider
     */
    void setProvider(boolean provider);

    /**
     * 是否是注入器
     *
     * @return
     */
    boolean isProvider();

    /**
     * 设置注入器类型
     *
     * @param providedType
     */
    void setProvidedType(Class<?> providedType);

}
