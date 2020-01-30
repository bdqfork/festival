package cn.bdqfork.web.proxy;

import cn.bdqfork.web.context.service.HessianMessageCodec;
import cn.bdqfork.web.context.service.MethodInvocation;
import cn.bdqfork.web.util.EventBusUtils;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;

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
        EventBus eventBus = vertx.eventBus();
        String address = EventBusUtils.getAddress(targetClass);
        return eventBus.rxRequest(address, methodInvocation, options)
                .toFlowable()
                .map(Message::body);
    }
}
