package cn.bdqfork.ioc.context;

import cn.bdqfork.ioc.annotation.*;
import cn.bdqfork.ioc.common.ScopeType;
import cn.bdqfork.ioc.container.*;
import cn.bdqfork.ioc.exception.SpringToyException;
import cn.bdqfork.ioc.exception.UnsatisfiedBeanException;
import cn.bdqfork.ioc.generator.BeanNameGenerator;
import cn.bdqfork.ioc.generator.SimpleBeanNameGenerator;
import cn.bdqfork.ioc.utils.ReflectUtil;

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
        this.resolving();
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
            String name = getName(candidate);
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
                beanDefination.setInjectorManager(new InjectorProvider(candidate, this.beanNameGenerator));

                beanContainer.register(beanDefination.getName(), beanDefination);
            }
        }
    }

    private String getName(Class<?> candidate) {
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

    private void resolving() throws UnsatisfiedBeanException {
        Map<String, BeanDefination> beanDefinationMap = beanContainer.getBeanDefinations();

        for (Map.Entry<String, BeanDefination> entry : beanDefinationMap.entrySet()) {

            BeanDefination beanDefination = entry.getValue();

            InjectorProvider injectorManager = beanDefination.getInjectorManager();
            if (injectorManager != null) {

                if (injectorManager.getConstructorParameterDatas() != null) {
                    for (InjectorData parameterInjectorData : injectorManager.getConstructorParameterDatas()) {
                        doResolving(beanDefination, injectorManager, parameterInjectorData);
                    }
                }

                if (injectorManager.getFieldInjectorDatas() != null) {
                    for (InjectorData fieldInjectorData : injectorManager.getFieldInjectorDatas()) {
                        doResolving(beanDefination, injectorManager, fieldInjectorData);
                    }
                }

                if (injectorManager.getMethodInjectorAttributes() != null) {
                    for (MethodInjectorAttribute methodInjectorAttributes : injectorManager.getMethodInjectorAttributes()) {
                        if (methodInjectorAttributes.getParameterInjectorDatas() != null) {
                            for (InjectorData parameterInjectorData : methodInjectorAttributes.getParameterInjectorDatas()) {
                                doResolving(beanDefination, injectorManager, parameterInjectorData);
                            }
                        }
                    }
                }
            }

        }
    }

    private void doResolving(BeanDefination beanDefination, InjectorProvider injectorManager, InjectorData injectorData) throws UnsatisfiedBeanException {
        BeanDefination ref = null;

        Map<String, BeanDefination> beanDefinationMap = beanContainer.getBeanDefinations();
        if (injectorData.getRefName() != null && beanDefinationMap.containsKey(injectorData.getRefName())) {
            ref = beanDefinationMap.get(injectorData.getRefName());
        } else if (beanDefinationMap.containsKey(injectorData.getDefalultName())) {
            ref = beanDefinationMap.get(injectorData.getDefalultName());
        } else {
            for (BeanDefination bean : beanDefinationMap.values()) {
                if (bean.isType(injectorData.getType())) {
                    ref = bean;
                    break;
                }
            }
        }

        if (ref == null) {
            throw new UnsatisfiedBeanException("unsatisfied bean , the bean named" + injectorData.getRefName() + " don't exists");
        } else if (injectorManager.hasDependence(beanDefination)) {
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
        return beanContainer.getBean(clazz);
    }

    @Override
    public <T> Map<String, T> getBeans(Class<T> clazz) throws SpringToyException {
        return beanContainer.getBeans(clazz);
    }

    @Override
    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

}
