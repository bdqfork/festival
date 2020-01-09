package cn.bdqfork.core.util;

import cn.bdqfork.core.exception.ResolvedException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author bdq
 * @since 2019/10/1
 */
public class ReflectUtils {
    private static final String FILE_PROTOCOL = "file";
    private static final String JAR_PROTOCOL = "jar";
    private static final String SUFFIX = ".class";

    /**
     * 根据包名获取获取Class
     *
     * @param packageName 包名
     * @return Set<Class < ?>> 包名为null或者空字符串，则返回空集合
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
                    Class clazz = Class.forName(className);
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
                    Class clazz = Class.forName(className);
                    classes.add(clazz);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classes;
    }

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

    public static Class<?> getActualType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
        return (Class<?>) type;
    }

    public static void makeAccessible(AccessibleObject accessibleObject) {
        accessibleObject.setAccessible(true);
    }

    public static boolean isBaseType(Class<?> clazz) {
        return clazz.equals(java.lang.Integer.class) ||
                clazz.equals(java.lang.Byte.class) ||
                clazz.equals(java.lang.Long.class) ||
                clazz.equals(java.lang.Double.class) ||
                clazz.equals(java.lang.Float.class) ||
                clazz.equals(java.lang.Character.class) ||
                clazz.equals(java.lang.Short.class) ||
                clazz.equals(java.lang.Boolean.class) ||
                clazz.equals(java.util.Map.class) ||
                clazz.isPrimitive() ||
                clazz.isArray();
    }
}
