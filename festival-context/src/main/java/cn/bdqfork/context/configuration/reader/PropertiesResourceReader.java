package cn.bdqfork.context.configuration.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author fbw
 * @since 2020/1/9
 */
public class PropertiesResourceReader extends AbstractResourceReader {
    private Properties properties;

    public PropertiesResourceReader(String resourcePath) throws IOException {
        super(resourcePath);
    }

    public PropertiesResourceReader() throws IOException {
        this(DEFAULT_CONFIG_NAME + ".properties");
    }

    @Override
    protected void load() throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(getResourcePath());
        properties = new Properties();
        if (inputStream != null) {
            properties.load(inputStream);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T doReadProperty(String propertyName, Class<T> type) {
        if (type == null) {
            return (T) properties.getProperty(propertyName);
        }

        if (type.getClassLoader() != null){
            throw new IllegalArgumentException(String.format("unsupport type %s!", type.getCanonicalName()));
        }

        if (type == Integer.class || type == int.class) {
            Object value = Integer.parseInt(properties.getProperty(propertyName));
            return (T) value;
        }

        if (type == Long.class || type == long.class) {
            Object value = Long.parseLong(properties.getProperty(propertyName));
            return (T) value;
        }

        if (type == Double.class || type == double.class) {
            Object value = Double.parseDouble(properties.getProperty(propertyName));
            return (T) value;
        }

        if (type == Float.class || type == float.class) {
            Object value = Float.parseFloat(properties.getProperty(propertyName));
            return (T) value;
        }

        if (type == Short.class || type == short.class) {
            Object value = Short.parseShort(properties.getProperty(propertyName));
            return (T) value;
        }

        if (type == Byte.class || type == byte.class) {
            Object value = Byte.parseByte(properties.getProperty(propertyName));
            return (T) value;
        }

        if (type == Character.class || type == char.class) {
            Object value = properties.getProperty(propertyName).toCharArray()[0];
            return (T) value;
        }

        if (type == Boolean.class || type == boolean.class) {
            Object value = Boolean.valueOf(properties.getProperty(propertyName));
            return (T) value;
        }

        return (T) properties.getProperty(propertyName);
    }
}
