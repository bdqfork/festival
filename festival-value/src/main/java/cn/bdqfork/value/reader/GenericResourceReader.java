package cn.bdqfork.value.reader;

import cn.bdqfork.core.util.FileUtils;

import java.io.IOException;

/**
 * @author bdq
 * @since 2020/1/9
 */
public class GenericResourceReader implements ResourceReader {
    private ResourceReader resourceReader;
    private static String defaultPath;

    static {
        if (FileUtils.isResourceExists(ResourceReader.DEFAULT_CONFIG_NAME + ".yaml")) {
            defaultPath = ResourceReader.DEFAULT_CONFIG_NAME + ".yaml";
        }
        if (FileUtils.isResourceExists(ResourceReader.DEFAULT_CONFIG_NAME + ".properties")) {
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
    public Object readProperty(String propertyName) throws Throwable {
        return resourceReader.readProperty(propertyName);
    }
}
