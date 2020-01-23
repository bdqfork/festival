package cn.bdqfork.model.configration;

import cn.bdqfork.value.Configration;
import cn.bdqfork.value.Value;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

/**
 * @author bdq
 * @since 2020/1/9
 */
@Singleton
@Configration(prefix = "server")
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

    public int getPort() {
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
}
