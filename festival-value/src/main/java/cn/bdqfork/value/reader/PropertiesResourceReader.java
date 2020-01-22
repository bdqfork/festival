package cn.bdqfork.value.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author fbw
 * @since 2020/1/9
 */
public class PropertiesResourceReader extends AbstractResourceReader {
    private static Properties properties;

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

    @Override
    public Object readProperty(String propertyName) throws Throwable {
        return properties.getProperty(propertyName);
    }
}
