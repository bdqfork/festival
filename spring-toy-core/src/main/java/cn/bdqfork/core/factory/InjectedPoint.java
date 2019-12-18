package cn.bdqfork.core.factory;

import cn.bdqfork.core.util.ReflectUtils;

import java.lang.reflect.Type;

/**
 * @author bdq
 * @since 2019/12/18
 */
public class InjectedPoint {
    private String beanName;
    private Type type;
    private boolean require;

    public InjectedPoint(Type type) {
        this(type, true);
    }

    public InjectedPoint(Type type, boolean require) {
        this("", type, require);
    }

    public InjectedPoint(String beanName, Type type, boolean require) {
        this.beanName = beanName;
        this.type = type;
        this.require = require;
    }

    public String getBeanName() {
        return beanName;
    }

    public boolean isRequire() {
        return require;
    }

    public Type getType() {
        return type;
    }

    public Class<?> getActualType() {
        return ReflectUtils.getActualType(type);
    }
}
