package cn.bdqfork.core.container;

import java.lang.reflect.Field;

/**
 * 属性依赖描述
 *
 * @author bdq
 * @since 2019-07-27
 */
public class FieldAttribute {
    /**
     * 依赖的Bean名称
     */
    private String beanName;
    /**
     * 依赖属性
     */
    private Field field;
    /**
     * 依赖类型，如果isProvider为true，则为获取真实类型
     */
    private Class<?> type;
    /**
     * 是否强制需要
     */
    private boolean isRequired;
    /**
     * 是否是provider
     */
    private boolean isProvider;

    public FieldAttribute(String beanName, Field field, Class<?> type, boolean isRequired, boolean isProvider) {
        this.beanName = beanName;
        this.field = field;
        this.type = type;
        this.isRequired = isRequired;
        this.isProvider = isProvider;
    }

    public String getBeanName() {
        return beanName;
    }

    public Field getField() {
        return field;
    }

    public boolean isRequired() {
        return isRequired;
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
