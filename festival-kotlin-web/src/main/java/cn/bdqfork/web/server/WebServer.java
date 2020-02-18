package cn.bdqfork.web.server;

/**
 * @author bdq
 * @since 2020/2/12
 */
public interface WebServer {
    void start() throws Exception;

    void stop() throws Exception;
}
