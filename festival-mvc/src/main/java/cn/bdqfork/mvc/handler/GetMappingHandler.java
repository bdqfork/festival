package cn.bdqfork.mvc.handler;

import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.mvc.annotation.GetMapping;
import io.vertx.ext.web.Router;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/21
 */
public class GetMappingHandler implements RouterMappingHandler {
    @Override
    public void handle(Router router, Object bean, String baseUrl, Method declaredMethod) {
        GetMapping getMapping = declaredMethod.getAnnotation(GetMapping.class);
        router.get(baseUrl + getMapping.value()).handler(routingContext ->
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
