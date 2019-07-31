package cn.bdqfork.core.container;

/**
 * 注入方法（构造方法）参数描述
 *
 * @author bdq
 * @since 2019-07-27
 */
public class ParameterAttribute {
    /**
     * 依赖Bean名称
     */
    private String beanName;
    /**
     * 参数类型
     */
    private Class<?> type;
    /**
     * 是否为provider
     */
    private boolean isProvider;

    public ParameterAttribute(String beanName, Class<?> type, boolean isProvider) {
        this.beanName = beanName;
        this.type = type;
        this.isProvider = isProvider;
    }

    public String getBeanName() {
        return beanName;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean isProvider() {
        return isProvider;
    }

}
