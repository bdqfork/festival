package cn.bdqfork.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * @author bdq
 * @since 2020/1/21
 */
public class AnnotatedElementUtils {

    public static <A extends Annotation> A getAnnotation(AnnotatedElement element, Class<A> target) {
        return element.getAnnotation(target);
    }

    public static <A extends Annotation> A getMergedAnnotation(AnnotatedElement element, Class<A> target) {
        A annotation = getAnnotation(element, target);
        if (annotation != null) {
            return annotation;
        }
        return findMetaAnnotation(element, target);
    }

    @SuppressWarnings("unchecked")
    private static <A extends Annotation> A findMetaAnnotation(AnnotatedElement element, Class<A> target) {
        for (Annotation annotation : element.getAnnotations()) {
            Class<? extends Annotation> metaAnnotation = annotation.annotationType();
            if (isMetaAnnotation(metaAnnotation)) {
                continue;
            }
            if (metaAnnotation == target) {
                return (A) annotation;
            }
            A meta = findMetaAnnotation(annotation.annotationType(), target);
            if (meta != null) {
                return meta;
            }
        }
        return null;
    }

    public static <A extends Annotation> boolean isAnnotationPresent(AnnotatedElement element, Class<A> target) {
        return getMergedAnnotation(element, target) != null;
    }

    public static boolean isMetaAnnotation(Class<? extends Annotation> metaAnnotation) {
        return metaAnnotation.getName().startsWith("java.lang.annotation");
    }

}
