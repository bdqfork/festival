package cn.bdqfork.context.configuration.reader;

import cn.bdqfork.core.util.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author bdq
 * @since 2020/1/9
 */
@Slf4j
public class GenericResourceReader implements ResourceReader {
    private ResourceReader resourceReader;
    private static String defaultPath = ResourceReader.DEFAULT_CONFIG_NAME + ".yaml";

    static {
        if (FileUtils.isResourceExists(ResourceReader.DEFAULT_CONFIG_NAME + ".yaml")) {
            if (log.isInfoEnabled()) {
                log.info("find config file {}, will use it !", ResourceReader.DEFAULT_CONFIG_NAME + ".yaml");
            }
            defaultPath = ResourceReader.DEFAULT_CONFIG_NAME + ".yaml";
        }
        if (FileUtils.isResourceExists(ResourceReader.DEFAULT_CONFIG_NAME + ".properties")) {
            if (log.isInfoEnabled()) {
                log.info("find config file {}, will use it !", ResourceReader.DEFAULT_CONFIG_NAME + ".properties");
            }
            defaultPath = ResourceReader.DEFAULT_CONFIG_NAME + ".properties";
        }
    }

    public GenericResourceReader() throws IOException {
        this(defaultPath);
    }

    public GenericResourceReader(String resourcePath) throws IOException {
        if (resourcePath.endsWith(".yaml")) {
            resourceReader = new YamlResourceReader(resourcePath);
            return;
        }
        if (resourcePath.endsWith(".properties")) {
            resourceReader = new PropertiesResourceReader(resourcePath);
            return;
        }
        throw new IllegalStateException(String.format("unsupport to read for file %s !", resourcePath));
    }

    @Override
    public <T> T readProperty(String propertyName, Class<T> type) {
        return resourceReader.readProperty(propertyName, type);
    }

    @Override
    public <T> T readProperty(String propertyName, T defaultValue) {
        T value = readProperty(propertyName,null);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }
}
