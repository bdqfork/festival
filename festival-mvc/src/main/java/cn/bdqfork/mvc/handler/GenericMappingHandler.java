package cn.bdqfork.mvc.handler;

import cn.bdqfork.mvc.annotation.DeleteMapping;
import cn.bdqfork.mvc.annotation.GetMapping;
import cn.bdqfork.mvc.annotation.PostMapping;
import cn.bdqfork.mvc.annotation.PutMapping;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/21
 */
public class GenericMappingHandler extends AbstractMappingHandler {
    private GetMappingHandler getMappingHandler;
    private PostMappingHandler postMappingHandler;
    private PutMappingHandler putMappingHandler;
    private DeleteMappingHandler deleteMappingHandler;

    public GenericMappingHandler(Vertx vertx) {
        super(vertx);
        getMappingHandler = new GetMappingHandler(vertx);
        postMappingHandler = new PostMappingHandler(vertx);
        putMappingHandler = new PutMappingHandler(vertx);
        deleteMappingHandler = new DeleteMappingHandler(vertx);
    }

    @Override
    protected void doMapping(Router router, Object bean, String baseUrl, Method declaredMethod) {
        if (declaredMethod.isAnnotationPresent(GetMapping.class)) {
            getMappingHandler.handle(router, bean, baseUrl, declaredMethod);
        }
        if (declaredMethod.isAnnotationPresent(PostMapping.class)) {
            postMappingHandler.handle(router, bean, baseUrl, declaredMethod);
        }
        if (declaredMethod.isAnnotationPresent(PutMapping.class)) {
            putMappingHandler.handle(router, bean, baseUrl, declaredMethod);
        }
        if (declaredMethod.isAnnotationPresent(DeleteMapping.class)) {
            deleteMappingHandler.handle(router, bean, baseUrl, declaredMethod);
        }
    }
}
