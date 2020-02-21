package cn.bdqfork.web.route.response;

import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.web.constant.ContentType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2020/2/15
 */
public class ResponseHandlerFactory {
    private Map<String, ResponseHandleStrategy> responseHandlerMap = new ConcurrentHashMap<>(16);

    public ResponseHandlerFactory() {
        registerResponseHandler(ContentType.PLAIN, new TextPlainResponseHandler());
        registerResponseHandler(ContentType.JSON, new JsonResponseHandler());
        registerResponseHandler(ContentType.XML, new XmlResponseHandler());
        registerResponseHandler(ContentType.HTML, new HtmlResponseHandler());
    }

    public void registerResponseHandler(String contentType, ResponseHandleStrategy responseHandleStrategy) {
        responseHandlerMap.put(contentType, responseHandleStrategy);
    }

    public ResponseHandleStrategy getResponseHandler(String contentType) {
        if (StringUtils.isEmpty(contentType) || !responseHandlerMap.containsKey(contentType)) {
            contentType = ContentType.JSON;
        }
        return responseHandlerMap.get(contentType);
    }
}
