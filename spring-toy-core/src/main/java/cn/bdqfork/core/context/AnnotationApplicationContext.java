package cn.bdqfork.core.context;


import cn.bdqfork.core.annotation.*;
import cn.bdqfork.core.common.ScopeType;
import cn.bdqfork.core.container.*;
import cn.bdqfork.core.exception.SpringToyException;
import cn.bdqfork.core.exception.UnsatisfiedBeanException;
import cn.bdqfork.core.generator.BeanNameGenerator;
import cn.bdqfork.core.generator.SimpleBeanNameGenerator;
import cn.bdqfork.core.utils.ReflectUtil;

import java.lang.reflect.*;
import java.util.*;

/**
 * ApplicationContext的实现类，负责扫描注解，并将bean注册到容器中
 *
 * @author bdq
 * @date 2019-02-12
 */
public class AnnotationApplicationContext implements ApplicationContext {
    private String[] scanPaths;
    private BeanContainer beanContainer;
    private BeanNameGenerator beanNameGenerator;

    public AnnotationApplicationContext(String... scanPaths) throws SpringToyException {
        if (scanPaths.length < 1) {
            throw new SpringToyException("the length of scanPaths is less than 1 ");
        }
        this.beanNameGenerator = new SimpleBeanNameGenerator();
        this.beanContainer = new BeanContainer();
        this.scanPaths = scanPaths;
        this.scan();
    }

    private void scan() throws SpringToyException {
        Set<Class<?>> candidates = new HashSet<>();
        for (String scanPath : scanPaths) {
            candidates.addAll(ReflectUtil.getClasses(scanPath));
        }
        for (Class<?> candidate : candidates) {
            if (candidate.isAnnotation() || candidate.isInterface() || Modifier.isAbstract(candidate.getModifiers())) {
                continue;
            }
            String name = getComponentName(candidate);
            if (name != null) {
                boolean isSingleton = true;

                Scope scope = candidate.getAnnotation(Scope.class);
                if (scope != null) {
                    if (ScopeType.PROTOTYPE.equals(scope.value())) {
                        isSingleton = false;
                    } else if (!ScopeType.SINGLETON.equals(scope.value())) {
                        throw new SpringToyException("the value of scope is error !");
                    }
                }

                if ("".equals(name)) {
                    name = this.beanNameGenerator.generateBeanName(candidate);
                }

                BeanDefination beanDefination = new BeanDefination(candidate, isSingleton, name);
                beanDefination.setInjectorProvider(new InjectorProvider(candidate, this.beanNameGenerator));

                beanContainer.register(beanDefination.getName(), beanDefination);
            }
        }

        Map<String, BeanDefination> beanDefinationMap = beanContainer.getBeanDefinations();
        for (Map.Entry<String, BeanDefination> entry : beanDefinationMap.entrySet()) {
            preInject(entry.getValue());
        }

    }

    private void preInject(BeanDefination beanDefination) throws SpringToyException {

        if (beanDefination.isPreSolved()) {
            return;
        }
        Class<?> superClass = beanDefination.getClazz().getSuperclass();
        if (superClass != null && superClass != Object.class) {

            for (BeanDefination bean : beanContainer.getBeans(superClass).values()) {
                if (bean != beanDefination) {
                    preInject(bean);
                }
            }
        }

        InjectorProvider injectorProvider = beanDefination.getInjectorProvider();
        if (injectorProvider != null) {

            if (injectorProvider.getConstructorParameterDatas() != null) {
                for (InjectorData parameterInjectorData : injectorProvider.getConstructorParameterDatas()) {
                    doPreInject(beanDefination, injectorProvider, parameterInjectorData, parameterInjectorData.isRequired());
                }
            }

            if (injectorProvider.getFieldInjectorDatas() != null) {
                for (InjectorData fieldInjectorData : injectorProvider.getFieldInjectorDatas()) {
                    doPreInject(beanDefination, injectorProvider, fieldInjectorData, fieldInjectorData.isRequired());
                }
            }

            if (injectorProvider.getMethodInjectorAttributes() != null) {
                for (MethodInjectorAttribute methodInjectorAttribute : injectorProvider.getMethodInjectorAttributes()) {
                    if (methodInjectorAttribute.getParameterInjectorDatas() != null) {
                        for (InjectorData parameterInjectorData : methodInjectorAttribute.getParameterInjectorDatas()) {
                            doPreInject(beanDefination, injectorProvider, parameterInjectorData, methodInjectorAttribute.isRequired());
                        }
                    }
                }
            }

        }

        beanDefination.setPreSolved(true);

    }

    private void doPreInject(BeanDefination beanDefination, InjectorProvider injectorProvider, InjectorData injectorData, boolean isRequired) throws UnsatisfiedBeanException {
        BeanDefination ref = null;

        Map<String, BeanDefination> beanDefinationMap = beanContainer.getBeanDefinations();
        if (injectorData.getRefName() != null && beanDefinationMap.containsKey(injectorData.getRefName())) {
            ref = beanDefinationMap.get(injectorData.getRefName());
        } else if (beanDefinationMap.containsKey(injectorData.getDefaultName())) {
            ref = beanDefinationMap.get(injectorData.getDefaultName());
        } else {
            for (BeanDefination bean : beanDefinationMap.values()) {
                if (bean.isType(injectorData.getType())) {
                    ref = bean;
                    break;
                } else if (bean.isSubType(injectorData.getType())) {
                    ref = bean;
                    break;
                }
            }
        }

        if (ref == null) {
            if (isRequired) {
                throw new UnsatisfiedBeanException("unsatisfied bean , the bean named " + injectorData.getType() + " don't exists");
            }
        } else if (beanDefination == ref || injectorProvider.hasDependence(beanDefination)) {
            throw new UnsatisfiedBeanException("unsatisfied bean , there two bean ref each other !");
        } else {
            injectorData.setBean(ref);
        }
    }

    @Override
    public Object getBean(String beanName) throws SpringToyException {
        return beanContainer.getBean(beanName);
    }

    @Override
    public <T> T getBean(Class<T> clazz) throws SpringToyException {
        BeanDefination beanDefination = beanContainer.getBean(clazz);
        if (beanDefination != null) {
            return (T) beanDefination.getInstance();
        }
        return null;
    }

    @Override
    public <T> Map<String, T> getBeans(Class<T> clazz) throws SpringToyException {
        Map<String, T> beanMap = new HashMap<>(8);
        for (Map.Entry<String, BeanDefination> entry : beanContainer.getBeans(clazz).entrySet()) {
            beanMap.put(entry.getKey(), (T) entry.getValue().getInstance());
        }
        return beanMap;
    }

    @Override
    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

    private String getComponentName(Class<?> candidate) {
        Component component = candidate.getAnnotation(Component.class);
        if (component != null) {
            return component.value();
        }
        Service service = candidate.getAnnotation(Service.class);
        if (service != null) {
            return service.value();
        }
        Repositorty repositorty = candidate.getAnnotation(Repositorty.class);
        if (repositorty != null) {
            return repositorty.value();
        }
        Controller controller = candidate.getAnnotation(Controller.class);
        if (controller != null) {
            return controller.value();
        }
        return null;
    }

}
