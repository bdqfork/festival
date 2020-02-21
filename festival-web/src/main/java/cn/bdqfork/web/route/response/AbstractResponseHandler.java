package cn.bdqfork.web.route.response;

import io.reactivex.Flowable;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * @author bdq
 * @since 2020/2/12
 */
public abstract class AbstractResponseHandler implements ResponseHandleStrategy {

    @Override
    public void handle(RoutingContext routingContext, Object result) throws Exception {
        HttpServerResponse httpServerResponse = routingContext.response();
        if (result == null) {
            httpServerResponse.end();
        }
        if (result instanceof Flowable<?>) {
            Flowable<?> flowable = (Flowable<?>) result;
            flowable.subscribe(res -> {
                if (res != null) {
                    doHandle(routingContext, res);
                }
            });
        } else {
            doHandle(routingContext, result);
        }
    }

    protected abstract void doHandle(RoutingContext routingContext, Object result) throws Exception;
}
