package cn.bdqfork.context.configuration.reader;

import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author bdq
 * @since 2020/1/9
 */
public abstract class AbstractResourceReader implements ResourceReader {
    private static final Logger log = LoggerFactory.getLogger(AbstractResourceReader.class);
    public static final String PROFILE = "profile";
    private final Map<String, Object> cache = Collections.synchronizedMap(new WeakHashMap<>());
    private String resourcePath;

    public AbstractResourceReader(String resourcePath) throws IOException {
        load(resourcePath);
        String profile = readProperty(PROFILE, String.class, "");
        if (!StringUtils.isEmpty(profile)) {
            cache.clear();
            String fileName = resourcePath.substring(0, resourcePath.lastIndexOf("."));
            String suffix = resourcePath.substring(resourcePath.lastIndexOf("."));
            resourcePath = fileName + '-' + profile + suffix;
            if (log.isInfoEnabled()) {
                log.info("active profile {}, load properties from {}!", profile, resourcePath);
            }
            load(resourcePath);
        }
        this.resourcePath = resourcePath;
    }

    protected abstract void load(String resourcePath) throws IOException;

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readProperty(String propertyName, Class<T> type) {
        if (cache.containsKey(propertyName)) {
            return (T) cache.get(propertyName);
        }
        Object value = doReadProperty(propertyName);
        if (value != null) {
            value = castIfNeed(type, value);
        }
        cache.put(propertyName, value);
        return (T) value;
    }

    private Object castIfNeed(Class<?> type, Object value) {
        if (ReflectUtils.isPrimitiveOrWrapper(type)) {
            return StringUtils.castToPrimitive(value.toString(), type);
        }

        return value;
    }

    @Override
    public <T> T readProperty(String propertyName, Class<T> type, T defaultValue) {
        T value = readProperty(propertyName, type);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    protected abstract Object doReadProperty(String propertyName);

    public String getResourcePath() {
        return resourcePath;
    }
}
