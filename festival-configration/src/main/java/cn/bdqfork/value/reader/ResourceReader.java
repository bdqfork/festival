package cn.bdqfork.value.reader;

/**
 * 资源读取器，用于读取资源文件
 *
 * @author bdq
 * @since 2020/1/9
 */
public interface ResourceReader {
    String DEFAULT_CONFIG_NAME = "application";

    /**
     * 根据配置项读取配置内容
     * @param propertyName 资源文件名称
     * @return 配置项内容
     * @throws Throwable
     */
    Object readProperty(String propertyName) throws Throwable;

}
