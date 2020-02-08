package cn.bdqfork.model.configuration;

/**
 * 工厂方法将要注册的bean
 * @author fbw
 * @since 2020/1/23
 */
public class FactoryBean {
    Server server;

    void setServer(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public String toString() {
        return "FactoryBean{" +
                "server=" + server +
                '}';
    }
}
