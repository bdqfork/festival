package cn.bdqfork.core.container;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * 构造器属性描述
 *
 * @author bdq
 * @date 2019-07-26
 */
public class ConstructorAttribute {
    /**
     * 构造方法
     */
    private Constructor<?> constructor;
    /**
     * 构造方法参数
     */
    private List<ParameterAttribute> args;

    public ConstructorAttribute(Constructor<?> constructor, List<ParameterAttribute> args) {
        this.constructor = constructor;
        this.args = args;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }


    public List<ParameterAttribute> getArgs() {
        return args;
    }

}
