package cn.bdqfork.mvc.handler;

import cn.bdqfork.mvc.annotation.GetMapping;
import cn.bdqfork.mvc.annotation.PostMapping;
import io.vertx.ext.web.Router;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/21
 */
public class GenericMappingHandler implements RouterMappingHandler {
    private GetMappingHandler getMappingHandler = new GetMappingHandler();
    private PostMappingHandler postMappingHandler = new PostMappingHandler();

    @Override
    public void handle(Router router, Object bean, String baseUrl, Method declaredMethod) {
        if (declaredMethod.isAnnotationPresent(GetMapping.class)) {
            getMappingHandler.handle(router, bean, baseUrl, declaredMethod);
        }
        if (declaredMethod.isAnnotationPresent(PostMapping.class)) {
            postMappingHandler.handle(router, bean, baseUrl, declaredMethod);
        }
    }
}
