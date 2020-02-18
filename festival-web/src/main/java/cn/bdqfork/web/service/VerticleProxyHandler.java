package cn.bdqfork.web.service;

import cn.bdqfork.web.util.EventBusUtils;
import io.reactivex.Flowable;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.reactivex.core.eventbus.EventBus;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/26
 */
public class VerticleProxyHandler implements InvocationHandler {
    private DeliveryOptions options;
    private Vertx vertx;
    private Class<?> targetClass;

    public VerticleProxyHandler(Vertx vertx, Class<?> targetClass) {
        this.vertx = vertx;
        this.targetClass = targetClass;
        this.options = new DeliveryOptions();
        this.options.setCodecName(HessianMessageCodec.NAME);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodInvocation methodInvocation = new MethodInvocation(method, args);
        EventBus eventBus = EventBus.newInstance(vertx.eventBus());
        String address = EventBusUtils.getAddress(targetClass);
        return eventBus.rxRequest(address, methodInvocation, options)
                .toFlowable()
                .flatMap(msg -> {
                    Object result = msg.body();
                    if (result instanceof Throwable) {
                        throw new IllegalStateException((Throwable) result);
                    }
                    return (Flowable<?>) result;
                });
    }
}
