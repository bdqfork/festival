package cn.bdqfork.context.configuration.reader;

import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.core.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author bdq
 * @since 2020/1/9
 */
public abstract class AbstractResourceReader implements ResourceReader {
    private final Map<String, Object> cache = Collections.synchronizedMap(new WeakHashMap<>());
    private String resourcePath;

    public AbstractResourceReader(String resourcePath) throws IOException {
        this.resourcePath = resourcePath;
        load();
    }

    protected abstract void load() throws IOException;

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
