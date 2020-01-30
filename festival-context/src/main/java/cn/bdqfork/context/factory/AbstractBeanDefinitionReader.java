package cn.bdqfork.context.factory;

import cn.bdqfork.configration.Configration;
import cn.bdqfork.configration.reader.ResourceReader;
import cn.bdqfork.context.annotation.ComponentScan;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.exception.ConflictedBeanException;
import cn.bdqfork.core.exception.ResolvedException;
import cn.bdqfork.core.exception.ScopeException;
import cn.bdqfork.core.factory.AbstractBeanFactory;
import cn.bdqfork.core.factory.BeanNameGenerator;
import cn.bdqfork.core.factory.SimpleBeanNameGenerator;
import cn.bdqfork.core.factory.definition.BeanDefinition;
import cn.bdqfork.core.util.AnnotationUtils;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.core.util.StringUtils;

import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2020/1/8
 */
public abstract class AbstractBeanDefinitionReader {
    private Map<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>(256);
    private ResourceReader resourceReader;
    /**
     * BeanName生成器
     */
    private BeanNameGenerator beanNameGenerator = new SimpleBeanNameGenerator();

    public void scan(String scanPath) throws BeansException {

        Set<Class<?>> beanClasses = scanBean(scanPath);

        scanImportBean(beanClasses);

        beanDefinitions.putAll(resolve(beanClasses));

    }

    private Set<Class<?>> scanBean(String scanPath) {
        Set<Class<?>> candidates = new HashSet<>();
        for (Class<?> candidate : ReflectUtils.getClasses(scanPath)) {

            if (candidate.isAnnotation() || candidate.isInterface() || Modifier.isAbstract(candidate.getModifiers())) {
                continue;
            }

            if (checkIfComponent(candidate)) {
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    private void scanImportBean(Set<Class<?>> beanClasses) {
        String[] scanPaths = beanClasses.stream().filter(this::checkScanBean)
                .map(beanClass -> AnnotationUtils.getMergedAnnotation(beanClass, ComponentScan.class))
                .filter(Objects::nonNull)
                .flatMap(componentScan -> Arrays.stream(componentScan.value()))
                .toArray(String[]::new);
        for (String path : scanPaths) {
            beanClasses.addAll(scanBean(path));
        }
    }

    private boolean checkScanBean(Class<?> beanClass) {
        return AnnotationUtils.isAnnotationPresent(beanClass, Configration.class)
                && AnnotationUtils.isAnnotationPresent(beanClass, ComponentScan.class);
    }


    protected Map<String, BeanDefinition> resolve(Set<Class<?>> beanClasses) throws ResolvedException, ConflictedBeanException {
        Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
        for (Class<?> clazz : beanClasses) {

            String beanName = resolveBeanName(clazz);

            BeanDefinition beanDefinition = createBeanDefinition(beanName, clazz);

            if (beanDefinitions.containsKey(beanDefinition.getBeanName())) {

                throw new ConflictedBeanException(String.format("the entity named %s has conflicted ! ", beanName));

            }
            beanDefinitions.put(beanDefinition.getBeanName(), beanDefinition);

        }

        Set<Class<?>> configBeanClasses = beanClasses.stream()
                .filter(beanClass -> AnnotationUtils.isAnnotationPresent(beanClass, Configration.class))
                .collect(Collectors.toSet());

        for (Class<?> configBeanClass : configBeanClasses) {
            resolveFactoryBean(beanDefinitions, configBeanClass);
        }
        return beanDefinitions;
    }

    private void resolveFactoryBean(Map<String, BeanDefinition> beanDefinitions, Class<?> configBeanClass) throws ResolvedException, ConflictedBeanException {
        for (Method method : configBeanClass.getDeclaredMethods()) {
            if (AnnotationUtils.isAnnotationPresent(method, Named.class)) {

                String methodName = method.getName();
                if (Modifier.isAbstract(method.getModifiers())) {
                    throw new ResolvedException(String.format("method %s.%s is abstract !",
                            method.getDeclaringClass().getCanonicalName(), methodName));
                }

                if (method.getGenericReturnType().getTypeName().equals("void")) {
                    throw new ResolvedException(String.format("factory method %s.%s should have a return value !",
                            method.getDeclaringClass().getCanonicalName(), methodName));
                }

                String scope = BeanDefinition.PROTOTYPE;

                if (AnnotationUtils.isAnnotationPresent(method, Singleton.class)) {
                    scope = BeanDefinition.SINGLETON;
                }

                String beanName;
                Named named = AnnotationUtils.getMergedAnnotation(method, Named.class);
                if (named == null || StringUtils.isEmpty(named.value())) {
                    beanName = getBeanNameGenerator().generateBeanName(method.getReturnType());
                } else {
                    beanName = named.value();
                }

                BeanDefinition beanDefinition = BeanDefinition.builder()
                        .setBeanName(beanName)
                        .setBeanClass(method.getReturnType())
                        .setScope(scope)
                        .setConstructor(method)
                        .build();

                if (beanDefinitions.containsKey(beanDefinition.getBeanName())) {
                    throw new ConflictedBeanException(String.format("the entity named %s has conflicted ! ", beanName));
                }

                beanDefinitions.put(beanDefinition.getBeanName(), beanDefinition);
            }
        }
    }

    protected abstract BeanDefinition createBeanDefinition(String beanName, Class<?> clazz) throws ScopeException;

    protected abstract String resolveBeanName(Class<?> clazz);

    public void resolveInjectedPoint(BeanDefinition beanDefinition, AbstractBeanFactory beanFactory) throws ResolvedException {

        //如果已经解析过了，则返回
        if (beanDefinition.isResolved()) {
            return;
        }

        resolveConstructor(beanDefinition, beanFactory);

        resolveField(beanDefinition, beanFactory);

        resolveMethod(beanDefinition, beanFactory);

        beanDefinition.setResolved(true);
    }

    protected abstract void resolveConstructor(BeanDefinition beanDefinition, AbstractBeanFactory beanFactory) throws ResolvedException;

    protected abstract void resolveField(BeanDefinition beanDefinition, AbstractBeanFactory beanFactory) throws ResolvedException;

    protected abstract void resolveMethod(BeanDefinition beanDefinition, AbstractBeanFactory beanFactory) throws ResolvedException;

    protected abstract boolean checkIfComponent(Class<?> candidate);

    public Map<String, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }

    public void setBeanDefinitions(Map<String, BeanDefinition> beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }

    public BeanNameGenerator getBeanNameGenerator() {
        return beanNameGenerator;
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

    public ResourceReader getResourceReader() {
        return resourceReader;
    }

    public void setResourceReader(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
    }
}
