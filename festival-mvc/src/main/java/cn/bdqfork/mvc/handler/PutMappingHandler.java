package cn.bdqfork.mvc.handler;

import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.mvc.annotation.GetMapping;
import cn.bdqfork.mvc.annotation.PutMapping;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Slf4j
public class PutMappingHandler extends AbstractMappingHandler {
    public PutMappingHandler(Vertx vertx) {
        super(vertx);
    }

    @Override
    protected void doMapping(Router router, Object bean, String baseUrl, Method declaredMethod) {
        PutMapping putMapping = declaredMethod.getAnnotation(PutMapping.class);
        String path = baseUrl + putMapping.value();
        if (log.isInfoEnabled()) {
            log.info("put mapping path:{} to {}:{}!", path, declaredMethod.getDeclaringClass()
                    .getCanonicalName(), ReflectUtils.getSignature(declaredMethod));
        }
        router.put(path).handler(routingContext -> this.invoke(bean, declaredMethod, routingContext));
    }
}
