package cn.bdqfork.web.route.response;

import cn.bdqfork.web.util.XmlUtils;
import io.vertx.ext.web.RoutingContext;

/**
 * @author bdq
 * @since 2020/2/20
 */
public class XmlResponseHandler extends AbstractResponseHandler {

    @Override
    protected void doHandle(RoutingContext routingContext, Object result) throws Exception {
        routingContext.response().end(XmlUtils.toXml(result));
    }

}
