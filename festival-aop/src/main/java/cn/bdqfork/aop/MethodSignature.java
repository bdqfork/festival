package cn.bdqfork.aop;

import cn.bdqfork.core.util.ReflectUtils;
import org.aspectj.lang.Signature;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class MethodSignature implements Signature {
    /**
     * 方法所属类
     */
    private Class<?> targetClass;
    /**
     * 方法
     */
    private Method method;

    public MethodSignature(Method method) {
        this.method = method;
        this.targetClass = method.getDeclaringClass();
    }

    @Override
    public String toShortString() {
        return ReflectUtils.getSignature(method);
    }

    @Override
    public String toLongString() {
        return targetClass.getTypeName() + "." + toShortString();
    }

    @Override
    public String getName() {
        return method.getName();
    }

    @Override
    public int getModifiers() {
        return method.getModifiers();
    }

    @Override
    public Class<?> getDeclaringType() {
        return targetClass;
    }

    @Override
    public String getDeclaringTypeName() {
        return getDeclaringType().getTypeName();
    }

    @Override
    public String toString() {
        return toLongString();
    }
}
