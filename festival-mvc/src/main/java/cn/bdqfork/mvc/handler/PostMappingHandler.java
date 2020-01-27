package cn.bdqfork.mvc.handler;

import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.mvc.annotation.PostMapping;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/21
 */
@Slf4j
public class PostMappingHandler extends AbstractMappingHandler {
    public PostMappingHandler(Vertx vertx) {
        super(vertx);
    }

    @Override
    protected void doMapping(Router router, Object bean, String baseUrl, Method declaredMethod) {
        PostMapping postMapping = declaredMethod.getAnnotation(PostMapping.class);
        String path = baseUrl + postMapping.value();
        if (log.isInfoEnabled()) {
            log.info("post mapping path:{} to {}:{}!", path, declaredMethod.getDeclaringClass()
                    .getCanonicalName(), ReflectUtils.getSignature(declaredMethod));
        }
        router.post(path).handler(routingContext -> invoke(bean, declaredMethod, routingContext));
    }
}
