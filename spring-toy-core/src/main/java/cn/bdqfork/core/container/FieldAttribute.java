package cn.bdqfork.core.container;

import java.lang.reflect.Field;

/**
 * @author bdq
 * @date 2019-07-27
 */
public class FieldAttribute {
    private String beanName;
    private Field field;
    /**
     * 类型，如果provider为true，则为真实类型
     */
    private Class<?> type;
    private boolean required;
    private boolean provider;

    public FieldAttribute(String beanName, Field field, Class<?> type, boolean required, boolean provider) {
        this.beanName = beanName;
        this.field = field;
        this.type = type;
        this.required = required;
        this.provider = provider;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean isProvider() {
        return provider;
    }

    public void setProvider(boolean provider) {
        this.provider = provider;
    }

}
