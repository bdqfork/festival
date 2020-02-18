package cn.bdqfork.web.service;

import cn.bdqfork.core.util.AopUtils;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.web.util.EventBusUtils;
import io.reactivex.Completable;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/26
 */
public class ServiceVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(ServiceVerticle.class);
    private DeliveryOptions options;
    private Object serviceBean;

    public ServiceVerticle(Object serviceBean) {
        this.serviceBean = serviceBean;
        this.options = new DeliveryOptions();
        this.options.setCodecName(HessianMessageCodec.NAME);
    }

    @Override
    public Completable rxStart() {
        return Completable.fromAction(() -> {
            EventBus eventBus = vertx.eventBus();
            Class<?> targetClass = AopUtils.getTargetClass(serviceBean);
            String address = EventBusUtils.getAddress(targetClass);
            eventBus.consumer(address)
                    .toFlowable()
                    .onBackpressureBuffer()
                    .subscribe(msg -> {
                        try {
                            MethodInvocation invocation = (MethodInvocation) msg.body();
                            String methodName = invocation.getMethodName();
                            Class<?>[] argumentClasses = invocation.getArgumentClasses();
                            Method method = serviceBean.getClass().getMethod(methodName, argumentClasses);
                            Object result = ReflectUtils.invokeMethod(serviceBean, method, invocation.getArguments());
                            msg.reply(result, options);
                        } catch (Exception e) {
                            msg.reply(e.getCause(), options);
                        }
                    });
            if (log.isInfoEnabled()) {
                log.info("deploy verticle service {}!", targetClass.getCanonicalName());
            }
        });
    }

}
