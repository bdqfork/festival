package cn.bdqfork.core.factory;

import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.util.ReflectUtils;

import javax.inject.Named;
import java.lang.reflect.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @since 2019/12/16
 */
public class DefaultInjectedPoint implements InjectedPoint {
    private Member member;
    private BeanNameGenerator beanNameGenerator;
    private List<String> names = new LinkedList<>();
    private List<Type> types = new LinkedList<>();

    public DefaultInjectedPoint(Member member) {
        this.member = member;
        this.beanNameGenerator = new SimpleBeanNameGenerator();
        resolve();
    }

    protected void resolve() {
        if (getMember() instanceof Constructor) {
            Constructor<?> constructor = (Constructor<?>) getMember();
            doResolve(constructor.getParameters());
        }
        if (getMember() instanceof Method) {
            Method method = (Method) getMember();
            doResolve(method.getParameters());
        }
        if (getMember() instanceof Field) {
            doResolve((Field) getMember());
        }
    }

    protected void doResolve(Parameter[] parameters) {
        for (Parameter parameter : parameters) {
            Type type = parameter.getParameterizedType();
            types.add(type);
            Class<?> actualType = ReflectUtils.getActualType(type);
            String name = beanNameGenerator.generateBeanName(actualType);
            names.add(name);
        }
    }

    protected void doResolve(Field field) {
        Type type = field.getGenericType();
        types.add(type);
        if (field.isAnnotationPresent(Named.class)) {
            Named named = field.getAnnotation(Named.class);
            names.add(named.value());
        } else {
            Class<?> actualType = ReflectUtils.getActualType(type);
            String name = beanNameGenerator.generateBeanName(actualType);
            names.add(name);
        }
    }

    @Override
    public void setMember(Member member) {
        this.member = member;
    }

    @Override
    public Member getMember() {
        return member;
    }

    @Override
    public String[] getInjectedNames() {
        return names.toArray(new String[0]);
    }

    @Override
    public Type[] getInjectedTypes() {
        return types.toArray(new Type[0]);
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }
}
