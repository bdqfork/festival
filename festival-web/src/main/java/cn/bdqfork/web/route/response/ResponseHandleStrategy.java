package cn.bdqfork.web.route.response;


import io.vertx.core.http.HttpServerResponse;

/**
 * @author bdq
 * @since 2020/1/30
 */
public interface ResponseHandleStrategy {

    void handle(HttpServerResponse httpServerResponse, Object result) throws Exception;
}
