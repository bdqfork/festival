package cn.bdqfork.core.factory;

/**
 * @author bdq
 * @since 2019/12/18
 */
public interface ManagerBeanFactory extends ConfigurableBeanFactory {

    /**
     * 注入Resource
     *
     * @param name
     * @param bean
     */
    void injectedResource(String name, Object bean);

    /**
     * 解析Resouce依赖，过程参考@Resource
     *
     * @param injectedPoint
     * @param bean
     * @return
     */
    Object resovleResource(InjectedPoint injectedPoint, Object bean);

    void executePostConstuct();

    void executePreDestroy();

}
