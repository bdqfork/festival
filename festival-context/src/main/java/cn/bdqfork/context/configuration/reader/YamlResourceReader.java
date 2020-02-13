package cn.bdqfork.context.configuration.reader;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * @author bdq
 * @since 2020/1/9
 */
public class YamlResourceReader extends AbstractResourceReader {
    private Map<String, Object> properties;

    public YamlResourceReader() throws IOException {
        this(DEFAULT_CONFIG_NAME + ".yaml");
    }

    public YamlResourceReader(String resourcePath) throws IOException {
        super(resourcePath);
    }

    @Override
    protected void load() throws IOException {
        Yaml yaml = new Yaml();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(getResourcePath());
        if (inputStream != null) {
            properties = yaml.load(inputStream);
        } else {
            properties = Collections.emptyMap();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T doReadProperty(String propertyName, Class<T> type) {
        String[] keys = propertyName.split("\\.");
        int len = keys.length - 1;
        Map<String, Object> map = properties;
        for (int i = 0; i < len; i++) {
            if (map.containsKey(keys[i])) {
                map = (Map<String, Object>) map.get(keys[i]);
            } else {
                return null;
            }
        }

        if (type == null) {
            return (T) map.get(keys[len]);
        }

        if (type.getClassLoader() != null){
            throw new IllegalArgumentException(String.format("unsupport type %s!", type.getCanonicalName()));
        }

        if (type == Integer.class || type == int.class) {
            Object value = Integer.parseInt(map.get(keys[len]).toString());
            return (T) value;
        }

        if (type == Long.class || type == long.class) {
            Object value = Long.parseLong(map.get(keys[len]).toString());
            return (T) value;
        }

        if (type == Double.class || type == double.class) {
            Object value = Double.parseDouble(map.get(keys[len]).toString());
            return (T) value;
        }

        if (type == Float.class || type == float.class) {
            Object value = Float.parseFloat(map.get(keys[len]).toString());
            return (T) value;
        }

        if (type == Short.class || type == short.class) {
            Object value = Short.parseShort(map.get(keys[len]).toString());
            return (T) value;
        }

        if (type == Byte.class || type == byte.class) {
            Object value = Byte.parseByte(map.get(keys[len]).toString());
            return (T) value;
        }

        if (type == Character.class || type == char.class) {
            Object value = map.get(keys[len]).toString().toCharArray()[0];
            return (T) value;
        }

        if (type == Boolean.class || type == boolean.class) {
            Object value = Boolean.valueOf(map.get(keys[len]).toString());
            return (T) value;
        }

        return (T) map.get(keys[len]);
    }
}
