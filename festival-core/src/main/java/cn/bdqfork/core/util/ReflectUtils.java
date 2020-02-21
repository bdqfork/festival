package cn.bdqfork.core.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019/10/1
 */
public class ReflectUtils {
    private static final String FILE_PROTOCOL = "file";
    private static final String JAR_PROTOCOL = "jar";
    private static final String SUFFIX = ".class";

    /**
     * 根据包名获取Class
     *
     * @param packageName 包名
     * @return 返回一个Class集合，如果包名为null或者空字符串，则返回空集合
     */
    public static Set<Class<?>> getClasses(String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return Collections.emptySet();
        }
        //将包名改为相对路径
        String packagePath = packageName.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<Class<?>> classes = new HashSet<>();
        try {
            //扫描包路径，返回资源的枚举
            Enumeration<URL> dirs = classLoader.getResources(packagePath);
            while (dirs.hasMoreElements()) {
                URL fileUrl = dirs.nextElement();
                String filePath = fileUrl.getPath();
                //判断资源类型
                if (FILE_PROTOCOL.equals(fileUrl.getProtocol())) {
                    //处理文件类型的Class
                    classes.addAll(getClassesByFilePath(filePath, packagePath));
                } else if (JAR_PROTOCOL.equals(fileUrl.getProtocol())) {
                    //处理Jar包中的Class
                    JarURLConnection jarURLConnection = (JarURLConnection) fileUrl.openConnection();
                    JarFile jarFile = jarURLConnection.getJarFile();
                    classes.addAll(getClassesByJar(jarFile));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static Set<Class<?>> getClassesByFilePath(String filePath, String packagePath) {
        File file = new File(filePath);
        Set<Class<?>> classes = new HashSet<>();
        File[] chirldFiles = file.listFiles();
        if (chirldFiles == null) {
            return classes;
        }
        for (File chirldFile : chirldFiles) {
            String path = FileUtils.getUniformAbsolutePath(chirldFile);
            if (!chirldFile.isDirectory() && path.endsWith(SUFFIX)) {
                String className = path.substring(path.indexOf(packagePath), path.lastIndexOf(SUFFIX))
                        .replaceAll("/", ".");
                try {
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                classes.addAll(getClassesByFilePath(path, packagePath));
            }
        }
        return classes;
    }

    private static Set<Class<?>> getClassesByJar(JarFile jarFile) {
        Set<Class<?>> classes = new HashSet<>();
        try {
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String entryName = jarEntry.getName();
                if (!jarEntry.isDirectory() && entryName.endsWith(SUFFIX)) {
                    String className = entryName.substring(0, entryName.lastIndexOf(SUFFIX))
                            .replaceAll("/", ".");
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * 获取方法签名，签名由方法名和参数类型决定
     *
     * @param method 方法
     * @return 方法签名字符串
     */
    public static String getSignature(Method method) {
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append(method.getName())
                .append("(");
        Class<?>[] parameters = method.getParameterTypes();

        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
                signBuilder.append(",");
            }
            signBuilder.append(parameters[i].getName());
        }
        signBuilder.append(")");
        return signBuilder.toString();
    }

    /**
     * 如果是泛型，则返回真实的泛型类型，否则返回原类型
     *
     * @param type 待解析类型
     * @return 类型数组
     */
    public static Type[] getActualType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getActualTypeArguments();
        }
        return new Type[]{type};
    }

    /**
     * 使得私有反射实例可以访问
     *
     * @param accessibleObject 私有反射实例
     */
    public static void makeAccessible(AccessibleObject accessibleObject) {
        accessibleObject.setAccessible(true);
    }

    /**
     * 是否是可注入的配置类型
     *
     * @param clazz 类型
     * @return true 或者 false
     */
    public static boolean isInjectableValueType(Class<?> clazz) {
        return isPrimitiveOrWrapper(clazz) ||
                clazz.equals(java.util.Map.class) ||
                clazz.isArray();
    }

    /**
     * 是否是基本类型以及包装类
     *
     * @param clazz 待判断的类
     * @return true 或者 false
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.equals(java.lang.Integer.class) ||
                clazz.equals(java.lang.Byte.class) ||
                clazz.equals(java.lang.Long.class) ||
                clazz.equals(java.lang.Double.class) ||
                clazz.equals(java.lang.Float.class) ||
                clazz.equals(java.lang.Character.class) ||
                clazz.equals(java.lang.Short.class) ||
                clazz.equals(java.lang.Boolean.class) ||
                clazz.equals(java.lang.String.class) ||
                clazz.isPrimitive();
    }

    public static boolean isCollection(Class<?> clazz) {
        return isSubType(clazz, Collection.class) || isMap(clazz);
    }

    public static boolean isMap(Class<?> clazz) {
        return isSubType(clazz, Map.class);
    }

    /**
     * 判断clazz是否为target类型或子类型，如果是，返回true，否则返回false
     *
     * @param clazz  待判断类型
     * @param target 目标类型
     * @return boolean
     */
    public static boolean isSubType(Class<?> clazz, Class<?> target) {
        return target.isAssignableFrom(clazz);
    }

    /**
     * 执行反射方法
     *
     * @param object 实例
     * @param method 方法
     * @param args   参数
     * @return 方法调用结果
     * @throws InvocationTargetException 方法执行异常
     */
    public static Object invokeMethod(Object object, Method method, Object... args) throws InvocationTargetException {
        makeAccessible(method);
        try {
            return method.invoke(object, args);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 设置属性值
     *
     * @param object 实例
     * @param field  属性
     * @param value  属性值
     */
    public static void setValue(Object object, Field field, Object value) {
        makeAccessible(field);
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 判断一个类是Java类型还是用户定义类型
     *
     * @param clz 类型
     * @return 是否Java类型
     */
    public static boolean isJavaClass(Class<?> clz) {
        return clz != null && clz.getClassLoader() == null;
    }

    /**
     * 判断方法返回值是否为void
     *
     * @param method 方法实例
     * @return 是否为void
     */
    public static boolean isReturnVoid(Method method) {
        return method.getReturnType() == Void.TYPE;
    }

    /**
     * 根据注解获取方法
     *
     * @param beanClass  类
     * @param annotation 注解
     * @return 注解的方法，或者null
     * @throws IllegalStateException 如果注解的方法超过一个，抛出异常
     */
    public static Method getMethodByAnnotation(Class<?> beanClass, Class<? extends Annotation> annotation) {
        List<Method> methods = Arrays.stream(beanClass.getDeclaredMethods())
                .filter(method -> AnnotationUtils.isAnnotationPresent(method, annotation))
                .collect(Collectors.toList());

        if (methods.size() > 1) {
            throw new IllegalStateException("");
        }

        if (methods.size() == 0) {
            return null;
        }

        return methods.get(0);
    }

}
