package cn.bdqfork.core.aop;

import org.aspectj.lang.Signature;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Collectors;

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
        return targetClass.getName() + "." + getName();
    }

    @Override
    public String toLongString() {
        String[] paramterNames = Arrays.stream(method.getParameters())
                .map(Parameter::getName)
                .toArray(String[]::new);
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (paramterNames.length > 0) {
            sb.append(paramterNames[0]);
            for (int i = 1; i < paramterNames.length; i++) {
                sb.append(",").append(paramterNames[i]);
            }
        }
        sb.append(")");
        return targetClass.getTypeName() + "." + getName() + sb.toString();
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
