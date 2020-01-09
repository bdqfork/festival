package cn.bdqfork.model.configration;

import cn.bdqfork.value.Configration;
import cn.bdqfork.value.Value;

import javax.annotation.ManagedBean;

/**
 * @author bdq
 * @since 2020/1/9
 */
@Configration(prefix = "server")
@ManagedBean
public class ServerConfig {
    @Value("localhost")
    private String localhost;
    @Value("port")
    private Integer port;

    public String getLocalhost() {
        return localhost;
    }

    public void setLocalhost(String localhost) {
        this.localhost = localhost;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
