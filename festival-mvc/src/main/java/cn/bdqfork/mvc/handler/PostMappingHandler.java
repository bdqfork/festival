package cn.bdqfork.mvc.handler;

import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.mvc.annotation.PostMapping;
import io.vertx.ext.web.Router;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/21
 */
public class PostMappingHandler implements RouterMappingHandler {
    @Override
    public void handle(Router router, Object bean, String baseUrl, Method declaredMethod) {
        PostMapping postMapping = declaredMethod.getAnnotation(PostMapping.class);
        router.post(baseUrl + postMapping.value()).handler(routingContext ->
                {
                    try {
                        ReflectUtils.invoke(bean, declaredMethod, routingContext);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
        );
    }
}
