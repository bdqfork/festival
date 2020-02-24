package cn.bdqfork.core.extension;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-08-20
 */
public class ExtensionLoader<T> {
    private static final String PREFIX = "META-INF/extensions/";
    private static final Map<String, ExtensionLoader<?>> CACHES = new ConcurrentHashMap<>();
    private final Map<Class<T>, String> classNames = new ConcurrentHashMap<>();
    private final Map<String, Class<T>> extensionClasses = new ConcurrentHashMap<>();

    private volatile Map<String, T> cacheExtensions;
    private Class<T> type;

    private ExtensionLoader(Class<T> type) {
        this.type = type;
    }

    /**
     * 获取扩展接口对应的ExtensionLoader
     *
     * @param clazz 扩展接口
     * @param <T>   Class类型
     * @return ExtensionLoader<T>
     */
    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> clazz) {
        String className = clazz.getName();

        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("Fail to create ExtensionLoader for class " + className
                    + ", class is not Interface !");
        }

        SPI spi = clazz.getAnnotation(SPI.class);

        if (spi == null) {
            throw new IllegalArgumentException("Fail to create ExtensionLoader for class " + className
                    + ", class is not annotated by @SPI !");
        }

        ExtensionLoader<T> extensionLoader = (ExtensionLoader<T>) CACHES.get(className);

        if (extensionLoader == null) {
            CACHES.putIfAbsent(className, new ExtensionLoader<>(clazz));
            extensionLoader = (ExtensionLoader<T>) CACHES.get(className);
        }

        return extensionLoader;
    }

    /**
     * 根据extensionName获取扩展实例
     *
     * @param extensionName 扩展名称
     * @return T
     */
    public T getExtension(String extensionName) {
        T extension = getExtensions().get(extensionName);
        if (extension != null) {
            return extension;
        }
        throw new IllegalStateException("No extension named " + extensionName + " for class " + type.getName() + "!");
    }

    /**
     * 获取所有扩展
     *
     * @return Map<String, T>
     */
    public Map<String, T> getExtensions() {
        if (cacheExtensions == null) {
            cacheExtensions = new ConcurrentHashMap<>();
            getExtensionClasses();

            for (Map.Entry<String, Class<T>> entry : extensionClasses.entrySet()) {
                Class<T> clazz = entry.getValue();
                T instance;
                try {
                    instance = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
                cacheExtensions.putIfAbsent(entry.getKey(), instance);
            }

        }
        return Collections.unmodifiableMap(cacheExtensions);
    }

    private void getExtensionClasses() {
        if (classNames.size() > 0) {
            return;
        }
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<java.net.URL> urlEnumeration = classLoader.getResources(PREFIX + type.getName());
            while (urlEnumeration.hasMoreElements()) {
                java.net.URL url = urlEnumeration.nextElement();
                if (url.getPath().isEmpty()) {
                    throw new IllegalArgumentException("Extension path " + PREFIX + type.getName() + " don't exsist !");
                }
                if (url.getProtocol().equals("file") || url.getProtocol().equals("jar")) {
                    URLConnection urlConnection = url.openConnection();
                    Reader reader = new InputStreamReader(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.equals("")) {
                            continue;
                        }
                        //过滤注释
                        if (line.contains("#")) {
                            line = line.substring(0, line.indexOf("#"));
                        }
                        String[] values = line.split("=");
                        String name = values[0].trim();
                        String impl = values[1].trim();
                        if (extensionClasses.containsKey(name)) {
                            throw new IllegalStateException("Duplicate extension named " + name);
                        }

                        @SuppressWarnings("unchecked")
                        Class<T> clazz = (Class<T>) classLoader.loadClass(impl);

                        classNames.putIfAbsent(clazz, name);
                        extensionClasses.putIfAbsent(name, clazz);
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Fail to get extension class from " + PREFIX + type.getName() + "!", e);
        }
    }

}
