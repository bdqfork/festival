package cn.bdqfork.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author bdq
 * @since 2020/1/22
 */
public class AnnotationUtils {
    private static final Map<CacheKey, Annotation> CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    public static <A extends Annotation> A getAnnotation(AnnotatedElement element, Class<A> target) {
        return element.getAnnotation(target);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A getMergedAnnotation(AnnotatedElement element, Class<A> target) {
        CacheKey cacheKey = new CacheKey(element, target);

        if (CACHE.containsKey(cacheKey)) {
            return (A) CACHE.get(cacheKey);
        }

        A annotation = getAnnotation(element, target);

        if (annotation != null) {
            return annotation;
        }

        List<Annotation> visted = new LinkedList<>();

        annotation = (A) findMetaAnnotation(visted, element, target);

        if (annotation == null) {
            CACHE.put(cacheKey, null);
            return null;
        }

        String[] methodNames = Arrays.stream(target.getMethods())
                .map(Method::getName)
                .toArray(String[]::new);

        Map<String, Object> memberValues = extractValues(visted, methodNames);

        Annotation proxy = getProxyAnnotation(target, annotation, memberValues);

        CACHE.put(cacheKey, proxy);

        return (A) proxy;
    }

    private static Annotation findMetaAnnotation(List<Annotation> visted, AnnotatedElement element, Class<? extends Annotation> target) {
        for (Annotation annotation : element.getAnnotations()) {
            Class<? extends Annotation> metaAnnotation = annotation.annotationType();
            if (isMetaAnnotation(metaAnnotation)) {
                continue;
            }
            if (metaAnnotation == target) {
                visted.add(annotation);
                return annotation;
            }
            Annotation meta = findMetaAnnotation(visted, metaAnnotation, target);
            if (meta != null) {
                visted.add(annotation);
                return meta;
            }
        }
        return null;
    }

    public static boolean isMetaAnnotation(Class<? extends Annotation> metaAnnotation) {
        return metaAnnotation.getName().startsWith("java.lang.annotation");
    }

    private static Map<String, Object> extractValues(List<Annotation> visted, String[] methodNames) {
        Map<String, Object> memberValues = new HashMap<>();

        for (Annotation vistedAnnotation : visted) {

            Class<? extends Annotation> vistedAnnotationClass = vistedAnnotation.getClass();

            for (String methodName : methodNames) {
                if (checkIfMetaMethod(methodName)) {
                    continue;
                }
                Method method = getTargetMethod(vistedAnnotationClass, methodName);
                Object value = getValue(vistedAnnotation, method);
                memberValues.put(methodName, value);
            }

        }
        return memberValues;
    }

    private static boolean checkIfMetaMethod(String targetMethodName) {
        return "equals".equals(targetMethodName) ||
                "toString".equals(targetMethodName) ||
                "hashCode".equals(targetMethodName) ||
                "annotationType".equals(targetMethodName);
    }

    private static Method getTargetMethod(Class<? extends Annotation> vistedAnnotationClass, String methodName) {
        try {
            return vistedAnnotationClass.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Object getValue(Annotation vistedAnnotation, Method method) {
        try {
            return ReflectUtils.invokeMethod(vistedAnnotation, method);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Annotation getProxyAnnotation(Class<? extends Annotation> target, Annotation annotation, Map<String, Object> memberValues) {
        MergedAnnotationHandler handler = new MergedAnnotationHandler(annotation, memberValues);
        return (Annotation) Proxy.newProxyInstance(target.getClassLoader(), new Class[]{target}, handler);
    }

    public static <A extends Annotation> boolean isAnnotationPresent(AnnotatedElement element, Class<A> target) {
        return getMergedAnnotation(element, target) != null;
    }

    private static class CacheKey {
        private AnnotatedElement element;
        private Class<? extends Annotation> annotation;

        public CacheKey(AnnotatedElement element, Class<? extends Annotation> annotation) {
            this.element = element;
            this.annotation = annotation;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return Objects.equals(element, cacheKey.element) &&
                    Objects.equals(annotation, cacheKey.annotation);
        }

        @Override
        public int hashCode() {
            return Objects.hash(element, annotation);
        }

        @Override
        public String toString() {
            return "CacheKey{" +
                    "element=" + element +
                    ", annotation=" + annotation +
                    '}';
        }
    }

    private static class MergedAnnotationHandler implements InvocationHandler {
        private Annotation annotation;
        private Map<String, Object> memberValues;

        public MergedAnnotationHandler(Annotation annotation, Map<String, Object> memberValues) {
            this.annotation = annotation;
            this.memberValues = memberValues;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();

            if ("equals".equals(methodName)) {
                return annotation.equals(args[0]);
            }
            if ("toString".equals(methodName)) {
                return annotation.toString();
            }
            if ("hashCode".equals(methodName)) {
                return annotation.hashCode();
            }
            if ("annotationType".equals(methodName)) {
                return annotation.annotationType();
            }

            return memberValues.get(methodName);
        }
    }

}
