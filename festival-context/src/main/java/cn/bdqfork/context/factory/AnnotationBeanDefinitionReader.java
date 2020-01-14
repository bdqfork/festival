package cn.bdqfork.context.factory;

import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.exception.ScopeException;
import cn.bdqfork.core.factory.*;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.factory.definition.ManagedBeanDefinition;
import cn.bdqfork.core.util.BeanUtils;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.value.Configration;
import cn.bdqfork.value.Value;
import cn.bdqfork.value.reader.ResourceReader;
import jdk.nashorn.internal.ir.IfNode;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Scope;
import javax.inject.Singleton;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bdq
 * @since 2020/1/8
 */
public class AnnotationBeanDefinitionReader extends AbstractBeanDefinitionReader {
    /**
     * 是否启用JSR250
     */
    private boolean JSR250;

    public AnnotationBeanDefinitionReader() {
        this(true);
    }

    public AnnotationBeanDefinitionReader(boolean JSR250) {
        this.JSR250 = JSR250;
    }

    protected BeanDefinition createBeanDefinition(String beanName, Class<?> clazz) throws ScopeException {

        if (clazz.isAnnotationPresent(Singleton.class)) {

            if (JSR250) {
                return new ManagedBeanDefinition(beanName, clazz, BeanDefinition.SINGLETON);
            }

            return new BeanDefinition(beanName, clazz, BeanDefinition.SINGLETON);

        } else {

            if (JSR250) {
                return new ManagedBeanDefinition(beanName, clazz, BeanDefinition.PROTOTYPE);
            }

            return new BeanDefinition(beanName, clazz, BeanDefinition.PROTOTYPE);
        }
    }

    @Override
    protected String resolveBeanName(Class<?> clazz) {
        String name = "";

        if (clazz.isAnnotationPresent(Named.class)) {

            name = clazz.getAnnotation(Named.class).value();

        } else if (JSR250 && clazz.isAnnotationPresent(ManagedBean.class)) {

            name = clazz.getAnnotation(ManagedBean.class).value();

        }
        if (StringUtils.isEmpty(name)) {

            name = getBeanNameGenerator().generateBeanName(clazz);

        }
        return name;
    }

    @Override
    protected void resolveConstructor(BeanDefinition beanDefinition, AbstractBeanFactory beanFactory) throws ResolvedException {
        Class<?> candidate = beanDefinition.getBeanClass();

        Constructor<?> constructor = resolveInjectedConstructor(candidate);

        if (constructor == null) {
            return;
        }

        MultInjectedPoint multInjectedPoint = new MultInjectedPoint();

        for (Type type : constructor.getGenericParameterTypes()) {

            InjectedPoint injectedPoint = new InjectedPoint(type);

            multInjectedPoint.addInjectedPoint(injectedPoint);

            String beanName = generateDependentName(type, getBeanDefinitions());

            beanDefinition.addDependOn(beanName);

            beanFactory.registerDependentForBean(beanDefinition.getBeanName(), beanName);
        }

        beanDefinition.setInjectedConstructor(multInjectedPoint);
    }

    private Constructor<?> resolveInjectedConstructor(Class<?> candidate) throws ResolvedException {

        Constructor<?>[] constructors = Arrays.stream(candidate.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
                .toArray(Constructor<?>[]::new);

        if (constructors.length > 1) {
            throw new ResolvedException("injected constructors are more than one !");
        }

        if (constructors.length == 1) {
            return constructors[0];
        }
        return null;
    }

    @Override
    protected void resolveField(BeanDefinition beanDefinition, AbstractBeanFactory beanFactory) throws ResolvedException {
        Class<?> candidate = beanDefinition.getBeanClass();
        Map<String, InjectedPoint> fieldInjectedPoints = new HashMap<>();

        for (Field field : candidate.getDeclaredFields()) {

            if (candidate.isAnnotationPresent(Configration.class) && checkIfInjectedProperty(field)) {

                if (Modifier.isFinal(field.getModifiers()) && !ReflectUtils.isBaseType(field.getType())) {
                    throw new ResolvedException(String.format("the field %s is final or not base type !", field.getName()));
                }

                ResourceReader resourceReader = getResourceReader();
                Configration configration = candidate.getAnnotation(Configration.class);
                Value value = field.getAnnotation(Value.class);
                String propertyKey = "";
                if (StringUtils.isEmpty(configration.prefix())) {
                    propertyKey = value.value();
                } else {
                    propertyKey = configration.prefix() + "." + value.value();
                }

                InjectedPoint injectedPoint = new InjectedPoint(field.getType(), true);

                Object propertyValue;
                try {
                    propertyValue = resourceReader.readProperty(propertyKey);
                } catch (Throwable throwable) {
                    throw new ResolvedException(throwable.getCause());
                }

                injectedPoint.setValue(propertyValue);

                fieldInjectedPoints.put(field.getName(), injectedPoint);

            } else if (checkIfInjectedPoint(field)) {

                if (Modifier.isFinal(field.getModifiers())) {
                    throw new ResolvedException(String.format("the field %s is final !", field.getName()));
                }

                Type type = field.getGenericType();

                InjectedPoint injectedPoint = getFieldInjectedPoint(field, type);

                fieldInjectedPoints.put(field.getName(), injectedPoint);

                String beanName = generateDependentName(type, getBeanDefinitions());

                if (beanDefinition.isPrototype()) {

                    beanDefinition.addDependOn(beanName);

                    beanFactory.registerDependentForBean(beanDefinition.getBeanName(), beanName);
                }
            }

        }

        beanDefinition.setInjectedFields(fieldInjectedPoints);
    }

    private boolean checkIfInjectedProperty(Field field) {
        return field.isAnnotationPresent(Value.class);
    }


    private InjectedPoint getFieldInjectedPoint(Field field, Type type) {
        if (JSR250 && field.isAnnotationPresent(Resource.class)) {

            Resource resource = field.getAnnotation(Resource.class);

            if (StringUtils.isEmpty(resource.name()) && resource.type() == Object.class) {
                return new InjectedPoint(field.getName(), true);
            }

            if (resource.type() == Object.class) {
                return new InjectedPoint(resource.name(), type, true);
            }

            return new InjectedPoint(resource.name(), resource.type(), true);
        }

        if (field.isAnnotationPresent(Named.class)) {

            Named named = field.getAnnotation(Named.class);

            return new InjectedPoint(named.value(), true);
        } else {

            return new InjectedPoint(type);
        }
    }

    @Override
    protected void resolveMethod(BeanDefinition beanDefinition, AbstractBeanFactory beanFactory) throws ResolvedException {
        Class<?> candidate = beanDefinition.getBeanClass();

        Map<String, InjectedPoint> methods = new HashMap<>();

        for (Method method : candidate.getDeclaredMethods()) {

            if (checkIfInjectedPoint(method)) {

                String methodName = method.getName();

                if (Modifier.isAbstract(method.getModifiers())) {
                    throw new ResolvedException(String.format("the method %s is abstract !", methodName));
                }

                if (!methodName.startsWith("set") || method.getParameterCount() != 1) {
                    throw new ResolvedException(String.format("the method %s is not setter !", methodName));
                }

                Type type = method.getGenericParameterTypes()[0];

                InjectedPoint injectedPoint = getSetterInjectedPoint(method, type);

                methods.put(methodName, injectedPoint);

                String beanName = generateDependentName(type, getBeanDefinitions());

                if (beanDefinition.isPrototype()) {
                    beanDefinition.addDependOn(beanName);
                    beanFactory.registerDependentForBean(beanDefinition.getBeanName(), beanName);
                }

            }

            if (JSR250 && method.isAnnotationPresent(PostConstruct.class)) {
                if (method.getParameterCount() > 0) {
                    throw new ResolvedException("the method annotated by @PostConstruct should hava no parameters !");
                }
                ManagedBeanDefinition managedBeanDefinition = (ManagedBeanDefinition) beanDefinition;
                managedBeanDefinition.setInitializingMethod(method.getName());
            }

            if (JSR250 && method.isAnnotationPresent(PreDestroy.class)) {
                if (method.getParameterCount() > 0) {
                    throw new ResolvedException("the method annotated by @PostConstruct should hava no parameters !");
                }
                ManagedBeanDefinition managedBeanDefinition = (ManagedBeanDefinition) beanDefinition;
                managedBeanDefinition.setDestroyMethod(method.getName());
            }

        }
        beanDefinition.setInjectedSetters(methods);
    }

    private InjectedPoint getSetterInjectedPoint(Method method, Type type) {
        if (JSR250 && method.isAnnotationPresent(Resource.class)) {
            Resource resource = method.getAnnotation(Resource.class);

            String name;
            if (StringUtils.isEmpty(resource.name())) {
                name = StringUtils.makeInitialLowercase(method.getName().substring(3));
            } else {
                name = resource.name();
            }

            if (resource.type() == Object.class) {
                return new InjectedPoint(name, type, true);
            }

            return new InjectedPoint(name, resource.type(), true);
        }

        if (method.isAnnotationPresent(Named.class)) {

            Named named = method.getAnnotation(Named.class);

            return new InjectedPoint(named.value(), true);
        } else {
            return new InjectedPoint(type);
        }
    }

    protected boolean checkIfInjectedPoint(AnnotatedElement annotatedElement) {
        if (JSR250) {
            return annotatedElement.isAnnotationPresent(Inject.class) || annotatedElement.isAnnotationPresent(Resource.class);
        } else {
            return annotatedElement.isAnnotationPresent(Inject.class);
        }
    }

    protected boolean checkIfComponent(Class<?> candidate) {
        if (JSR250) {
            return candidate.isAnnotationPresent(Named.class) || candidate.isAnnotationPresent(ManagedBean.class);
        }
        return candidate.isAnnotationPresent(Named.class);
    }

    private String generateDependentName(Type type, Map<String, BeanDefinition> definitionMap) {
        Class<?> actualType = (Class<?>) ReflectUtils.getActualType(type)[0];
        if (actualType.isInterface()) {
            for (Map.Entry<String, BeanDefinition> entry : definitionMap.entrySet()) {
                BeanDefinition beanDefinition = entry.getValue();
                if (BeanUtils.checkIsInstance(actualType, beanDefinition.getBeanClass())) {
                    return entry.getKey();
                }
            }
        }
        return getBeanNameGenerator().generateBeanName(actualType);
    }

}
