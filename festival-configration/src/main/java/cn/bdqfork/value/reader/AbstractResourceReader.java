package cn.bdqfork.value.reader;

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
    public <T> T readProperty(String propertyName) {
        if (cache.containsKey(propertyName)) {
            return (T) cache.get(propertyName);
        }
        T value = doReadProperty(propertyName);
        cache.put(propertyName, value);
        return value;
    }

    protected abstract <T> T doReadProperty(String propertyName);

    public String getResourcePath() {
        return resourcePath;
    }
}
