package cn.bdqfork.core.container;

import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @date 2019-02-13
 */
public class ParameterInjectorData extends AbstractInjectorData {
    private Parameter parameter;

    public ParameterInjectorData(String defalultName, String refName, boolean required, Parameter parameter) {
        super(defalultName, refName, required);
        this.parameter = parameter;
    }

    @Override
    public Class<?> getType() {
        return parameter.getType();
    }

}
