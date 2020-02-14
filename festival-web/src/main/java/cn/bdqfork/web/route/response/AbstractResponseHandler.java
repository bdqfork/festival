package cn.bdqfork.web.route.response;

import io.reactivex.Flowable;
import io.vertx.reactivex.core.http.HttpServerResponse;

/**
 * @author bdq
 * @since 2020/2/12
 */
public abstract class AbstractResponseHandler implements ResponseHandleStrategy {

    @Override
    public void handle(HttpServerResponse httpServerResponse, String contentType, Object result) throws Exception {
        if (result == null) {
            httpServerResponse.end();
        }
        if (result instanceof Flowable<?>) {
            Flowable<?> flowable = (Flowable<?>) result;
            flowable.subscribe(res -> {
                if (res != null) {
                    doHandle(httpServerResponse, contentType, res);
                }
            });
        } else {
            doHandle(httpServerResponse, contentType, result);
        }
    }

    protected abstract void doHandle(HttpServerResponse httpServerResponse, String contentType, Object result) throws Exception;
}
