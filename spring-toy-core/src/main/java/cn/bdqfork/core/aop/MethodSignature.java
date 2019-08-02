package cn.bdqfork.core.aop;

import org.aspectj.lang.Signature;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 方法签名
 *
 * @author bdq
 * @since 2019-07-30
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

    public MethodSignature(Class<?> targetClass, Method method) {
        this.targetClass = targetClass;
        this.method = method;
    }

    @Override
    public String toShortString() {
        Class<?>[] parameterTypes = method.getParameterTypes();
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (parameterTypes.length > 0) {
            sb.append(parameterTypes[0]);
            for (int i = 1; i < parameterTypes.length; i++) {
                sb.append(",").append(parameterTypes[i]);
            }
        }
        sb.append(")");
        return getName() + sb.toString();
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
    public Class getDeclaringType() {
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
