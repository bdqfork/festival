package cn.bdqfork.web.route.response;


import io.vertx.ext.web.RoutingContext;

/**
 * @author bdq
 * @since 2020/2/12
 */
public class TextPlainResponseHandler extends AbstractResponseHandler {

    @Override
    protected void doHandle(RoutingContext routingContext, Object result) throws Exception {
        routingContext.response().end(result.toString());
    }

}
