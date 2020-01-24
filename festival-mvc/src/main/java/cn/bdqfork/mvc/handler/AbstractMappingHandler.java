package cn.bdqfork.mvc.handler;

import cn.bdqfork.core.util.ReflectUtils;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/24
 */
public abstract class AbstractMappingHandler implements RouterMappingHandler {
    protected Vertx vertx;

    public AbstractMappingHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(Router router, Object bean, String baseUrl, Method declaredMethod) {
        doMapping(router, bean, baseUrl, declaredMethod);
    }

    protected abstract void doMapping(Router router, Object bean, String baseUrl, Method declaredMethod);

    protected Object invoke(Object bean, Method method, Object... args) {
        try {
            return ReflectUtils.invokeMethod(bean, method, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
