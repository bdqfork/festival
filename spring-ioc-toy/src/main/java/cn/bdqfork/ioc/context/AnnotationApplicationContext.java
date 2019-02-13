package cn.bdqfork.ioc.context;

import cn.bdqfork.ioc.annotation.*;
import cn.bdqfork.ioc.common.Const;
import cn.bdqfork.ioc.container.BeanContainer;
import cn.bdqfork.ioc.container.BeanDefination;
import cn.bdqfork.ioc.container.DependenceData;
import cn.bdqfork.ioc.exception.SpringToyException;
import cn.bdqfork.ioc.exception.UnsatisfiedBeanException;
import cn.bdqfork.ioc.generator.BeanNameGenerator;
import cn.bdqfork.ioc.generator.SimpleBeanNameGenerator;
import cn.bdqfork.ioc.utils.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
                Map<String, DependenceData> dependenceDataMap = getDependenceDataMap(candidate);
                register(candidate, name, dependenceDataMap);
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

    private Map<String, DependenceData> getDependenceDataMap(Class<?> candidate) throws SpringToyException {
        Map<String, DependenceData> dependenceDatas = new HashMap<>(8);

        for (Field field : candidate.getDeclaredFields()) {
            field.setAccessible(true);

            AutoWired autoWired = field.getAnnotation(AutoWired.class);
            if (autoWired != null) {
                if (Modifier.isFinal(field.getModifiers())) {
                    throw new SpringToyException("the field: " + field.getName() + "is final , it can't be injected !");
                }
                String refName = null;

                Qualifier qualifier = field.getAnnotation(Qualifier.class);
                if (qualifier != null) {
                    refName = qualifier.value();
                }

                String defaultName = beanNameGenerator.generateBeanName(field.getType());
                dependenceDatas.put(field.getName(), new DependenceData(defaultName, refName, field));
            }
        }

        for (Method method : candidate.getDeclaredMethods()) {

        }
        return dependenceDatas;
    }

    private void register(Class<?> candidate, String name, Map<String, DependenceData> dependenceDataMap) throws SpringToyException {
        boolean isSingleton = true;

        Scope scope = candidate.getAnnotation(Scope.class);
        if (scope != null) {
            if (Const.PROTOTYPE.equals(scope.value())) {
                isSingleton = false;
            } else if (!Const.SINGLETON.equals(scope.value())) {
                throw new SpringToyException("the value of scope is error !");
            }
        }

        if ("".equals(name)) {
            name = this.beanNameGenerator.generateBeanName(candidate);
        }

        BeanDefination beanDefination = new BeanDefination(candidate, isSingleton, name);
        beanDefination.setDependenceDataMap(dependenceDataMap);
        beanContainer.register(beanDefination.getName(), beanDefination);
    }


    private void resolving() throws UnsatisfiedBeanException {
        Map<String, BeanDefination> beanDefinationMap = beanContainer.getBeanDefinations();

        for (Map.Entry<String, BeanDefination> entry : beanDefinationMap.entrySet()) {

            BeanDefination beanDefination = entry.getValue();

            for (DependenceData dependenceData : beanDefination.getDependenceDataMap().values()) {

                Field field = dependenceData.getField();
                field.setAccessible(true);

                BeanDefination ref = getRefBeanDefination(beanDefinationMap, dependenceData, field);

                if (ref == null) {
                    throw new UnsatisfiedBeanException("unsatisfied bean , the bean named" + dependenceData.getRefName() + " don't exists");
                } else if (ref.hasDependence(beanDefination)) {
                    throw new UnsatisfiedBeanException("unsatisfied bean , there two bean ref each other !");
                } else {
                    dependenceData.setBean(ref);
                }
            }
        }
    }

    private BeanDefination getRefBeanDefination(Map<String, BeanDefination> beanDefinationMap, DependenceData dependenceData, Field field) {
        BeanDefination ref = null;

        if (dependenceData.getRefName() != null && beanDefinationMap.containsKey(dependenceData.getRefName())) {
            ref = beanDefinationMap.get(dependenceData.getRefName());
        } else if (beanDefinationMap.containsKey(dependenceData.getDefalultName())) {
            ref = beanDefinationMap.get(dependenceData.getDefalultName());
        } else {
            for (BeanDefination bean : beanDefinationMap.values()) {
                if (bean.isType(field.getType())) {
                    ref = bean;
                    break;
                }
            }
        }

        return ref;
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
