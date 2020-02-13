package cn.bdqfork.model.configuration;

import cn.bdqfork.context.configuration.Configuration;
import cn.bdqfork.context.configuration.Value;

/**
 * @author fbw
 * @since 2020/2/13
 */
@Configuration(location = "JdbcConfig.properties")
public class CustomLocationConfig {
    @Value("driver")
    private String driver;
    @Value("port")
    private int port;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
