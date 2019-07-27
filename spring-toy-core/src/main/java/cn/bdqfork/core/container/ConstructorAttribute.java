package cn.bdqfork.core.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * @author bdq
 * @date 2019-07-26
 */
public class ConstructorAttribute {
    /**
     * 构造方法
     */
    private Constructor<?> constructor;
    /**
     * 参数
     */
    private List<ParameterAttribute> args;

    public ConstructorAttribute(Constructor<?> constructor, List<ParameterAttribute> args) {
        this.constructor = constructor;
        this.args = args;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    public List<ParameterAttribute> getArgs() {
        return args;
    }

    public void setArgs(List<ParameterAttribute> args) {
        this.args = args;
    }
}
