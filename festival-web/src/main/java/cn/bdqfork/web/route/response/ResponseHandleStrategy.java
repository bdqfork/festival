package cn.bdqfork.web.route.response;

import io.vertx.reactivex.core.http.HttpServerResponse;

/**
 * @author bdq
 * @since 2020/1/30
 */
public interface ResponseHandleStrategy {
    String DEFAULT_CONTENT_TYPE = "application/json";

    void handle(HttpServerResponse httpServerResponse, String contentType, Object result) throws Exception;
}
