package cn.bdqfork.core.container.resolver;

import cn.bdqfork.core.container.*;
import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.exception.ScopeException;

import javax.inject.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * 解析BeanDefinition
 *
 * @author bdq
 * @since 2019-02-22
 */
public class BeanDefinitionResolver implements Resolver<Map<String, BeanDefinition>> {
    /**
     * BeanName生成器
     */
    private BeanNameGenerator beanNameGenerator;
    /**
     * 待解析的类
     */
    private Collection<Class<?>> classes;

    public BeanDefinitionResolver(BeanNameGenerator beanNameGenerator, Collection<Class<?>> classes) {
        this.beanNameGenerator = beanNameGenerator;
        this.classes = classes;
    }

    @Override
    public Map<String, BeanDefinition> resolve() throws ResolvedException {
        Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
        for (Class<?> clazz : classes) {
            BeanDefinition beanDefinition = createBeanDefinition(clazz);
            doResolve(beanDefinition);
            beanDefinitions.put(beanDefinition.getBeanName(), beanDefinition);
        }
        return beanDefinitions;
    }

    private BeanDefinition createBeanDefinition(Class<?> clazz) throws ScopeException {
        String name = clazz.getAnnotation(Named.class).value();
        if ("".equals(name)) {
            name = this.beanNameGenerator.generateBeanName(clazz);
        }
        if (clazz.getAnnotation(Singleton.class) == null) {
            return new BeanDefinition(name, clazz, BeanDefinition.SINGLETON);
        } else if (clazz.isAnnotationPresent(Scope.class)) {
            //todo:添加异常信息
            throw new ScopeException("");
        } else {
            return new BeanDefinition(name, clazz);
        }
    }

    private void doResolve(BeanDefinition beanDefinition) throws ResolvedException {
        //如果已经解析过了，则返回
        if (beanDefinition.isResolved()) {
            return;
        }
        //优先解析父类
        Class<?> superClass = beanDefinition.getBeanClass().getSuperclass();
        if (superClass != null && superClass != Object.class) {
            for (Class<?> clazz : classes) {
                if (clazz == superClass) {
                    doResolve(createBeanDefinition(clazz));
                }
            }
        }

        resolveConstructorInfo(beanDefinition);

        resolveFieldInfo(beanDefinition);

        resolveMethodInfo(beanDefinition);

        beanDefinition.setResolved(true);

    }

    private void resolveConstructorInfo(BeanDefinition beanDefinition) throws ResolvedException {
        Class<?> candidate = beanDefinition.getBeanClass();
        Constructor<?>[] constructors = Arrays.stream(candidate.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
                .toArray(Constructor<?>[]::new);
        if (constructors.length > 1) {
            //todo:添加异常信息
            throw new ResolvedException("");
        } else if (constructors.length == 1) {
            List<ParameterAttribute> parameterAttributes = resolveParameterAttributes(constructors[0].getParameters());
            parameterAttributes.forEach(parameterAttribute -> beanDefinition.addDependOn(parameterAttribute.getBeanName()));
            ConstructorAttribute constructorAttribute = new ConstructorAttribute(constructors[0], parameterAttributes);
            beanDefinition.setConstructorAttribute(constructorAttribute);
        }
        try {
            Constructor<?> constructor = candidate.getDeclaredConstructor();
            ConstructorAttribute constructorAttribute = new ConstructorAttribute(constructor, null);
            beanDefinition.setConstructorAttribute(constructorAttribute);
        } catch (NoSuchMethodException e) {
            throw new ResolvedException(e);
        }

    }

    private void resolveFieldInfo(BeanDefinition beanDefinition) throws ResolvedException {
        Class<?> candidate = beanDefinition.getBeanClass();
        List<FieldAttribute> fieldAttributes = new LinkedList<>();

        for (Field field : candidate.getDeclaredFields()) {
            field.setAccessible(true);

            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {

                if (Modifier.isFinal(field.getModifiers())) {
                    throw new ResolvedException(String.format("the field %s is final !", field.getName()));
                }

                //获取依赖类型
                Class<?> type = field.getType();
                boolean isProvider = isProvider(type);
                if (isProvider) {
                    type = getActualType((ParameterizedType) field.getGenericType());
                }

                //Qualifier优先级比Named高
                String refName = getIfNamed(type, field.getAnnotation(Named.class));

                FieldAttribute fieldAttribute = new FieldAttribute(refName, field, type, true, isProvider);
                fieldAttributes.add(fieldAttribute);
            }
        }
        beanDefinition.setFieldAttributes(fieldAttributes);
    }

    private String getIfNamed(Class<?> clazz, Named named) {
        //获取依赖的BeanName
        String refName = beanNameGenerator.generateBeanName(clazz);
        if (named != null) {
            refName = named.value();
        }
        return refName;
    }

    private void resolveMethodInfo(BeanDefinition beanDefinition) throws ResolvedException {
        Class<?> candidate = beanDefinition.getBeanClass();
        Method[] methods = candidate.getDeclaredMethods();
        List<MethodAttribute> methodAttributes = new LinkedList<>();
        for (Method method : methods) {

            method.setAccessible(true);
            Inject inject = method.getAnnotation(Inject.class);

            if (inject != null) {
                String methodName = method.getName();
                if (Modifier.isAbstract(method.getModifiers())) {
                    throw new ResolvedException(String.format("the method %s is abstract !", methodName));
                }

                if (!methodName.startsWith("set")) {
                    throw new ResolvedException(String.format("the method %s is not setter !", methodName));
                }

                List<ParameterAttribute> parameterAttributes = resolveParameterAttributes(method.getParameters());

                MethodAttribute methodAttribute = new MethodAttribute(method, parameterAttributes, true);
                methodAttributes.add(methodAttribute);
            }
        }
        beanDefinition.setMethodAttributes(methodAttributes);
    }

    private List<ParameterAttribute> resolveParameterAttributes(Parameter[] parameters) throws ResolvedException {
        List<ParameterAttribute> parameterAttributes = new LinkedList<>();

        for (Parameter parameter : parameters) {
            //获取依赖类型
            Class<?> type = parameter.getType();
            boolean isProvider = isProvider(type);
            if (isProvider) {
                type = getActualType((ParameterizedType) parameter.getParameterizedType());
            }
            //获取依赖BeanName
            String refName = getIfNamed(type, parameter.getAnnotation(Named.class));

            ParameterAttribute parameterAttribute = new ParameterAttribute(refName, type, isProvider(parameter.getType()));
            parameterAttributes.add(parameterAttribute);
        }
        return parameterAttributes;
    }

    private Class<?> getActualType(ParameterizedType type) throws ResolvedException {
        Class<?> providedType;
        try {
            providedType = Class.forName(type.getActualTypeArguments()[0].getTypeName());
        } catch (ClassNotFoundException e) {
            throw new ResolvedException(String.format("class %s is not found !", type.getTypeName()), e);
        }
        return providedType;
    }

    private boolean isProvider(Class<?> clazz) {
        return clazz == ObjectFactory.class || clazz == Provider.class;
    }

}
