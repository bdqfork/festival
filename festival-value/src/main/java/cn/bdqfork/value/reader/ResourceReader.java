package cn.bdqfork.value.reader;

/**
 * @author bdq
 * @since 2020/1/9
 */
public interface ResourceReader {
    String DEFAULT_CONFIG_NAME = "application";

    Object readProperty(String propertyName) throws Throwable;

}
