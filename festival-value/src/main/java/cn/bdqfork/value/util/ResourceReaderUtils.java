package cn.bdqfork.value.util;

import cn.bdqfork.core.util.FileUtils;
import cn.bdqfork.value.reader.ResourceReader;

import java.net.URL;

/**
 * @author bdq
 * @since 2020/1/9
 */
public class ResourceReaderUtils {

    public static String getDefaultConfigFile() {

        if (FileUtils.isResourceExists(ResourceReader.DEFAULT_CONFIG_NAME + ".yaml")) {
            return ResourceReader.DEFAULT_CONFIG_NAME + ".yaml";
        }

        if (FileUtils.isResourceExists(ResourceReader.DEFAULT_CONFIG_NAME + ".properties")) {
            return ResourceReader.DEFAULT_CONFIG_NAME + ".properties";
        }

        return "";
    }

}
