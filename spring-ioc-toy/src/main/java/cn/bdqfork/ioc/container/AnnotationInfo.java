package cn.bdqfork.ioc.container;

import cn.bdqfork.ioc.annotation.Component;
import cn.bdqfork.ioc.annotation.Controller;
import cn.bdqfork.ioc.annotation.Repositorty;
import cn.bdqfork.ioc.annotation.Service;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author bdq
 * @date 2019-02-12
 */
public class AnnotationInfo {
    private Set<String> annotationNames;
    private Map<String, Map<String, Object>> attributeMap;

    public AnnotationInfo(Class<?> clazz) {

    }

    private void scanAnnotations(Class<?> clazz) {
        scanNames(clazz);
    }

    private void scanNames(Class<?> clazz) {
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            annotationNames.add(annotation.annotationType().getName());
        }
    }

    private void scanAttribute(Class<?> clazz, Annotation annotation) {
        if (annotation instanceof Component) {
            Component component = (Component) annotation;
            Map<String, Object> atrributes = new HashMap<>();
            atrributes.put("name", component.value());
            attributeMap.put(component.annotationType().getName(), atrributes);
        } else if (annotation instanceof Service) {
            Service component = (Service) annotation;
            Map<String, Object> atrributes = new HashMap<>();
            atrributes.put("name", component.value());
            attributeMap.put(component.annotationType().getName(), atrributes);
        } else if (annotation instanceof Repositorty) {
            Repositorty component = (Repositorty) annotation;
            Map<String, Object> atrributes = new HashMap<>();
            atrributes.put("name", component.value());
            attributeMap.put(component.annotationType().getName(), atrributes);
        } else if (annotation instanceof Controller) {
            Controller component = (Controller) annotation;
            Map<String, Object> atrributes = new HashMap<>();
            atrributes.put("name", component.value());
            attributeMap.put(component.annotationType().getName(), atrributes);
        }
    }

}
