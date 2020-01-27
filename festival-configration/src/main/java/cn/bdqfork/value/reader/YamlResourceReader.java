package cn.bdqfork.value.reader;

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
    protected <T> T doReadProperty(String propertyName) {
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
        return (T) map.get(keys[len]);
    }
}
