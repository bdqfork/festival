package cn.bdqfork.model.configration;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

/**
 * @author bdq
 * @since 2020/1/9
 */
@ManagedBean
public class Server {
    @Inject
    private ServerConfig serverConfig;

    public ServerConfig getServerConfig() {
        return serverConfig;
    }
}
