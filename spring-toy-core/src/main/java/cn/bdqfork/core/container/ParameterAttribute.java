package cn.bdqfork.core.container;

import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @date 2019-07-27
 */
public class ParameterAttribute {
    private String beanName;
    private Parameter parameter;
    private Class<?> type;
    private boolean provider;

    public ParameterAttribute(String beanName, Parameter parameter, Class<?> type, boolean provider) {
        this.beanName = beanName;
        this.parameter = parameter;
        this.type = type;
        this.provider = provider;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
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
