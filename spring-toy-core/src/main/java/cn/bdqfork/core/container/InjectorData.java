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
    void setBean(BeanDefination bean);

    /**
     * 返回依赖的bean
     *
     * @return
     */
    BeanDefination getBean();

    /**
     * 获取依赖的默认名称
     *
     * @return
     */
    String getDefalultName();

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
     * @param beanDefination
     * @return
     */
    boolean isMatch(BeanDefination beanDefination);

    /**
     * 是否必须
     *
     * @return
     */
    boolean isRequired();
}
