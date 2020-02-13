package cn.bdqfork.web.route.response;

import cn.bdqfork.core.util.StringUtils;
import io.vertx.reactivex.core.http.HttpServerResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2020/2/12
 */
public class GenericResponseHandler implements ResponseHandleStrategy {
    private Map<String, ResponseHandleStrategy> responseHandlerMap = new ConcurrentHashMap<>(16);

    public GenericResponseHandler() {
        registerResponseHandler(JsonResponseHandler.CONTENT_TYPE, new JsonResponseHandler());
        registerResponseHandler(TextPlainResponseHandler.CONTENT_TYPE, new TextPlainResponseHandler());
    }

    public void registerResponseHandler(String contentType, ResponseHandleStrategy responseHandleStrategy) {
        responseHandlerMap.put(contentType, responseHandleStrategy);
    }


    @Override
    public void handle(HttpServerResponse httpServerResponse, String contentType, Object result) throws Exception {
        if (result == null) {
            return;
        }

        if (StringUtils.isEmpty(contentType) || !responseHandlerMap.containsKey(contentType)) {
            contentType = DEFAULT_CONTENT_TYPE;
        }

        ResponseHandleStrategy strategy = responseHandlerMap.get(contentType);
        strategy.handle(httpServerResponse, contentType, result);
    }
}
