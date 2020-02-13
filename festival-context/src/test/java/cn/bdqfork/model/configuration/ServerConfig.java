package cn.bdqfork.model.configuration;

import cn.bdqfork.context.annotation.ComponentScan;
import cn.bdqfork.context.configuration.Configuration;
import cn.bdqfork.context.configuration.Value;

import javax.inject.Singleton;
import java.util.List;

/**
 * @author bdq
 * @since 2020/1/9
 */
@Singleton
@ComponentScan("cn.bdqfork.model.bean.normal")
@Configuration(prefix = "server")
public class ServerConfig {
    @Value("localhost")
    private String localhost;
    @Value("port")
    private int port;
    @Value("names")
    private List<String> names;

    public String getLocalhost() {
        return localhost;
    }

    public void setLocalhost(String localhost) {
        this.localhost = localhost;
    }

    public double getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "localhost='" + localhost + '\'' +
                ", port=" + port +
                ", names=" + names +
                '}';
    }
}
