package cn.bdqfork.core.factory;

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
    private List<Class<?>> types = new LinkedList<>();

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
            Class<?> type = parameter.getType();
            types.add(type);
            String name = beanNameGenerator.generateBeanName(type);
            names.add(name);
        }
    }

    protected void doResolve(Field field) {
        Class<?> type = field.getType();
        types.add(type);
        if (field.isAnnotationPresent(Named.class)) {
            Named named = type.getAnnotation(Named.class);
            names.add(named.value());
        } else {
            String name = beanNameGenerator.generateBeanName(type);
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
    public Class<?>[] getInjectedTypes() {
        return types.toArray(new Class[0]);
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }
}
