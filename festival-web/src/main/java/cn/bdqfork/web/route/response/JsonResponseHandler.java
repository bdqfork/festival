package cn.bdqfork.web.route.response;

import io.vertx.core.json.Json;
import io.vertx.reactivex.core.http.HttpServerResponse;

/**
 * @author bdq
 * @since 2020/2/12
 */
public class JsonResponseHandler extends AbstractResponseHandler {

    @Override
    protected void doHandle(HttpServerResponse response, Object result) throws Exception {
        response.end(Json.encodePrettily(result));
    }
}
