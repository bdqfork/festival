package cn.bdqfork.mvc.handler;

import io.vertx.ext.web.Router;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/21
 */
public interface RouterMappingHandler {
    void handle(Router router, Object bean, String baseUrl, Method declaredMethod);
}
