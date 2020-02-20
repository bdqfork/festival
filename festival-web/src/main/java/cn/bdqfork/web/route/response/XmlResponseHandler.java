package cn.bdqfork.web.route.response;

import cn.bdqfork.web.util.XmlUtils;
import io.vertx.core.http.HttpServerResponse;

/**
 * @author bdq
 * @since 2020/2/20
 */
public class XmlResponseHandler extends AbstractResponseHandler {
    @Override
    protected void doHandle(HttpServerResponse httpServerResponse, Object result) throws Exception {
        httpServerResponse.end(XmlUtils.toXml(result));
    }
}
