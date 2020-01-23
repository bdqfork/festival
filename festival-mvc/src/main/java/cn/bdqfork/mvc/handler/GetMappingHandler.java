package cn.bdqfork.mvc.handler;

import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.mvc.annotation.GetMapping;
import io.vertx.reactivex.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Slf4j
public class GetMappingHandler implements RouterMappingHandler {
    @Override
    public void handle(Router router, Object bean, String baseUrl, Method declaredMethod) {
        GetMapping getMapping = declaredMethod.getAnnotation(GetMapping.class);
        String path = baseUrl + getMapping.value();
        if (log.isInfoEnabled()) {
            log.info("mapping path:{} to {}:{}!", path, declaredMethod.getDeclaringClass()
                    .getCanonicalName(), ReflectUtils.getSignature(declaredMethod));
        }
        router.get(path).handler(routingContext ->
                {
                    try {
                        ReflectUtils.invokeMethod(bean, declaredMethod, routingContext);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
        );
    }
}
