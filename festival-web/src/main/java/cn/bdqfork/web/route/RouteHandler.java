package cn.bdqfork.web.route;

import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.web.constant.ContentType;
import cn.bdqfork.web.route.message.HttpMessageHandler;
import cn.bdqfork.web.route.response.ResponseHandlerFactory;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/2/21
 */
public class RouteHandler implements Handler<RoutingContext> {
    private HttpMessageHandler httpMessageHandler;
    private ResponseHandlerFactory responseHandlerFactory;
    private Method method;
    private Object bean;

    public RouteHandler(HttpMessageHandler httpMessageHandler, ResponseHandlerFactory responseHandlerFactory, Method method, Object bean) {
        this.httpMessageHandler = httpMessageHandler;
        this.responseHandlerFactory = responseHandlerFactory;
        this.method = method;
        this.bean = bean;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        try {
            Object[] args = httpMessageHandler.handle(routingContext, method.getParameters());
            Object result = ReflectUtils.invokeMethod(bean, method, args);
            if (ReflectUtils.isReturnVoid(method)) {
                return;
            }
            String contentType = routingContext.getAcceptableContentType();
            if (result instanceof ModelAndView) {
                contentType = ContentType.HTML;
            }
            responseHandlerFactory.getResponseHandler(contentType).handle(routingContext, result);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
