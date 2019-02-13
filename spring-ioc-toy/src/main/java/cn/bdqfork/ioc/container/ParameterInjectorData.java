package cn.bdqfork.ioc.container;

import java.lang.reflect.Parameter;

/**
 * @author bdq
 * @date 2019-02-13
 */
public class ParameterInjectorData extends AbstractInjectorData {
    private Parameter parameter;

    public ParameterInjectorData(String defalultName, String refName, Parameter parameter) {
        super(defalultName, refName);
        this.parameter = parameter;
    }

    @Override
    public Class<?> getType() {
        return parameter.getType();
    }

}
