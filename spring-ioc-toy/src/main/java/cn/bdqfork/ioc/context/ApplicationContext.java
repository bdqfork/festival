package cn.bdqfork.ioc.context;

import cn.bdqfork.ioc.annotation.*;
import cn.bdqfork.ioc.container.BeanContainer;
import cn.bdqfork.ioc.container.BeanDefination;
import cn.bdqfork.ioc.exception.SpringToyException;
import cn.bdqfork.ioc.generator.BeanNameGenerator;
import cn.bdqfork.ioc.generator.SimpleBeanNameGenerator;
import cn.bdqfork.ioc.utils.ReflectUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author bdq
 * @date 2019-02-12
 */
public class ApplicationContext {
    private String[] scanPaths;
    private BeanContainer beanContainer;
    private BeanNameGenerator beanNameGenerator;

    public ApplicationContext(String... scanPaths) throws SpringToyException {
        if (scanPaths.length < 1) {
            throw new SpringToyException("the length of scanPaths is less than 1 ");
        }
        this.beanNameGenerator = new SimpleBeanNameGenerator();
        this.beanContainer = new BeanContainer();
        this.scanPaths = scanPaths;
        this.scan();
        this.inject();
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

    private void register(Class<?> candidate, String name, Map<String, Map<String, Object>> refs) throws SpringToyException {
        boolean isSingleton = true;
        Scope scope = candidate.getAnnotation(Scope.class);
        if (scope != null) {
            if ("prototype".equals(scope.value())) {
                isSingleton = false;
            }
        }
        BeanDefination beanDefination = new BeanDefination(candidate, isSingleton, name, this.beanNameGenerator);
        beanDefination.setRefs(refs);
        beanContainer.register(beanDefination.getName(), beanDefination);
    }

    private void scan() throws SpringToyException {
        Set<Class<?>> candidates = new HashSet<>();
        for (String scanPath : scanPaths) {
            candidates.addAll(ReflectUtil.getClasses(scanPath));
        }
        for (Class<?> candidate : candidates) {
            String name = match(candidate);
            if (name != null) {
                Map<String, Map<String, Object>> refs = getRefs(candidate);
                register(candidate, name, refs);
            }
        }
    }

    private Map<String, Map<String, Object>> getRefs(Class<?> candidate) {
        Field[] fields = candidate.getFields();
        Map<String, Map<String, Object>> refs = new HashMap<>(8);
        for (Field field : fields) {
            AutoWired autoWired = field.getAnnotation(AutoWired.class);
            if (autoWired != null) {
                Map<String, Object> refAttribute = new HashMap<>(8);
                refAttribute.put("field", field);
                String name = beanNameGenerator.generateBeanName(field.getType());
                Qualifier qualifier = field.getAnnotation(Qualifier.class);
                if (qualifier != null) {
                    name = qualifier.value();
                }
                refAttribute.put("name", name);
                refs.put(field.getName(), refAttribute);
            }
        }
        return refs;
    }

    private String match(Class<?> candidate) {
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

    private void inject() {

    }

    public Object getBean(String name) {
        return beanContainer.getBean(name);
    }

    public <T> T getBean(Class<T> clazz) {
        return beanContainer.getBean(clazz);
    }

    public <T> Map<String, T> getBeans(Class<T> clazz) {
        return beanContainer.getBeans(clazz);
    }

}
