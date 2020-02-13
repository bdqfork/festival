package cn.bdqfork.web.route.response;

import io.vertx.reactivex.core.http.HttpServerResponse;

/**
 * @author bdq
 * @since 2020/2/12
 */
public class TextPlainResponseHandler extends AbstractResponseHandler {
    public static final String CONTENT_TYPE = "text/plain";

    @Override
    protected void doHandle(HttpServerResponse response, String contentType, Object result) throws Exception {
        response.end(result.toString());
    }
}
