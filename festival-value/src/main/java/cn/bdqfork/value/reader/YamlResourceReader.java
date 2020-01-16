package cn.bdqfork.value.reader;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
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
        URL url = YamlResourceReader.class.getClassLoader().getResource(getResourcePath());
        if (url != null) {
            properties = yaml.load(new FileInputStream(url.getFile()));
        } else {
            properties = Collections.emptyMap();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object readProperty(String propertyName) throws Throwable {
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
        return map.get(keys[len]);
    }
}
