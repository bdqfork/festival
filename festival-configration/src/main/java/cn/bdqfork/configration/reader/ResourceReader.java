package cn.bdqfork.configration.reader;

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
     *
     * @param propertyName 资源名称
     * @return 配置项内容
     */
    <T> T readProperty(String propertyName);

    /**
     * 根据配置项读取配置内容
     *
     * @param propertyName 资源名称
     * @param defaultValue 默认值
     * @param <T>
     * @return
     */
    <T> T readProperty(String propertyName, T defaultValue);

}
