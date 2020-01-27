package cn.bdqfork.mvc.handler;

import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.mvc.annotation.DeleteMapping;
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
public class DeleteMappingHandler extends AbstractMappingHandler {
    public DeleteMappingHandler(Vertx vertx) {
        super(vertx);
    }

    @Override
    protected void doMapping(Router router, Object bean, String baseUrl, Method declaredMethod) {
        DeleteMapping deleteMapping = declaredMethod.getAnnotation(DeleteMapping.class);
        String path = baseUrl + deleteMapping.value();
        if (log.isInfoEnabled()) {
            log.info("delete mapping path:{} to {}:{}!", path, declaredMethod.getDeclaringClass()
                    .getCanonicalName(), ReflectUtils.getSignature(declaredMethod));
        }
        router.delete(path).handler(routingContext -> this.invoke(bean, declaredMethod, routingContext));
    }
}
