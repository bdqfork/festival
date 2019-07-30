package cn.bdqfork.core.aop;

import org.aspectj.lang.Signature;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-07-30
 */
public class MethodSignature implements Signature {
    private Class<?> targetClass;
    private Method method;

    public MethodSignature(Class<?> targetClass, Method method) {
        this.targetClass = targetClass;
        this.method = method;
    }

    @Override
    public String toShortString() {
        return null;
    }

    @Override
    public String toLongString() {
        return null;
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
    public Class getDeclaringType() {
        return targetClass;
    }

    @Override
    public String getDeclaringTypeName() {
        return getDeclaringType().getTypeName();
    }

    @Override
    public String toString() {
        return "MethodSignature{" +
                "targetClass=" + targetClass +
                ", method=" + method +
                '}';
    }
}
