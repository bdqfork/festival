package cn.bdqfork.web.route.response;


import io.vertx.core.http.HttpServerResponse;

/**
 * @author bdq
 * @since 2020/2/12
 */
public class TextPlainResponseHandler extends AbstractResponseHandler {

    @Override
    protected void doHandle(HttpServerResponse response, Object result) throws Exception {
        response.end(result.toString());
    }
}
