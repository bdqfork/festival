package cn.bdqfork.ioc.utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @date 2019-02-03
 */
public class ReflectUtil {
    private static final String FILE_PROTOCOL = "file";
    private static final String JAR_PROTOCOL = "jar";
    private static final String SUFFIX = ".class";

    public static List<Class<?>> getClasses(String packageName) {
        if (packageName == null || packageName.equals("")) {
            return Collections.emptyList();
        }
        String packagePath = packageName.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        List<Class<?>> classes = new ArrayList<>();
        try {
            Enumeration<URL> dirs = classLoader.getResources(packagePath);
            while (dirs.hasMoreElements()) {
                URL fileUrl = dirs.nextElement();
                String filePath = fileUrl.getPath();
                if (FILE_PROTOCOL.equals(fileUrl.getProtocol())) {
                    classes.addAll(getClassesByFilePath(filePath, packagePath));
                } else if (JAR_PROTOCOL.equals(fileUrl.getProtocol())) {
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

    private static List<Class<?>> getClassesByFilePath(String filePath, String packagePath) {
        File file = new File(filePath);
        List<Class<?>> classes = new ArrayList<>();
        File[] chirldFiles = file.listFiles();
        if (chirldFiles == null) {
            return classes;
        }
        for (File chirldFile : chirldFiles) {
            String path = chirldFile.getAbsolutePath();
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

    private static List<Class<?>> getClassesByJar(JarFile jarFile) {
        List<Class<?>> classes = new ArrayList<>();
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

    public static List<Class<?>> getClassByAnnotation(String packageName, Annotation annotation) {
        List<Class<?>> classes = getClasses(packageName);
        return classes.stream()
                .filter(clazz -> clazz.getAnnotation(annotation.getClass()) != null)
                .collect(Collectors.toList());
    }
}
